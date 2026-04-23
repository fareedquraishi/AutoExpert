package com.autoexpert.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.AttendanceQueueDao
import com.autoexpert.app.data.local.dao.SaleEntryQueueDao
import com.autoexpert.app.data.remote.api.SupabaseApi
import com.autoexpert.app.data.remote.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val api: SupabaseApi,
    private val saleDao: SaleEntryQueueDao,
    private val attendanceDao: AttendanceQueueDao,
    private val gson: Gson,
) : CoroutineWorker(ctx, params) {

    private val apiKey  = BuildConfig.SUPABASE_ANON_KEY
    private val authHeader = "Bearer $apiKey"

    override suspend fun doWork(): Result {
        return try {
            syncSaleEntries()
            syncAttendance()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private suspend fun syncSaleEntries() {
        val pending = saleDao.getPending()
        for (entry in pending) {
            try {
                val remote = RemoteSaleEntry(
                    baId             = entry.baId,
                    stationId        = entry.stationId,
                    customerName     = entry.customerName,
                    mobile           = entry.mobile,
                    plateNumber      = entry.plateNumber,
                    vehicleTypeId    = entry.vehicleTypeId,
                    isRepeat         = entry.isRepeat,
                    competitorBrandId= entry.competitorBrandId,
                    isApplicator     = entry.isApplicator,
                    notes            = entry.notes,
                    totalLitres      = entry.totalLitres,
                    totalCommission  = entry.totalCommission,
                    entryDate        = entry.entryDate,
                )
                val resp = api.postSaleEntry(remote, apiKey, authHeader)
                if (resp.isSuccessful) {
                    val remoteId = resp.body()?.firstOrNull()?.id
                    // Post items if present
                    if (entry.itemsJson != "[]" && remoteId != null) {
                        val itemType = object : TypeToken<List<Map<String, Any>>>() {}.type
                        val items = gson.fromJson<List<Map<String, Any>>>(entry.itemsJson, itemType)
                        val remoteItems = items.map { item ->
                            RemoteSaleEntryItem(
                                saleEntryId = remoteId,
                                skuId        = item["skuId"] as String,
                                qtyPacks     = (item["qtyPacks"] as Double).toInt(),
                                qtyLitres    = item["qtyLitres"] as Double,
                                unitPrice    = item["unitPrice"] as Double,
                                commission   = item["commission"] as Double,
                            )
                        }
                        api.postSaleEntryItems(remoteItems, apiKey, authHeader)
                    }
                    saleDao.updateSyncStatus(entry.localId, "synced", remoteId)
                } else {
                    saleDao.updateSyncStatus(entry.localId, "failed")
                }
            } catch (e: Exception) {
                saleDao.updateSyncStatus(entry.localId, "failed")
            }
        }
    }

    private suspend fun syncAttendance() {
        val pending = attendanceDao.getPending()
        for (att in pending) {
            try {
                val remote = RemoteAttendance(
                    baId           = att.baId,
                    attendanceDate = att.attendanceDate,
                    status         = att.status,
                    markedByGps    = att.markedByGps,
                    latitude       = att.latitude,
                    longitude      = att.longitude,
                )
                val resp = api.postAttendance(remote, apiKey, authHeader)
                if (resp.isSuccessful) {
                    val remoteId = resp.body()?.firstOrNull()?.id
                    attendanceDao.updateSyncStatus(att.localId, "synced", remoteId)
                } else {
                    attendanceDao.updateSyncStatus(att.localId, "failed")
                }
            } catch (e: Exception) {
                attendanceDao.updateSyncStatus(att.localId, "failed")
            }
        }
    }

    companion object {
        const val WORK_NAME = "AutoExpertSync"

        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun triggerImmediateSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
