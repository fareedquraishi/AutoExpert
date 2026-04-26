#!/usr/bin/env python3
content = '''package com.autoexpert.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.*
import com.autoexpert.app.data.local.entity.*
import com.autoexpert.app.data.remote.api.SupabaseApi
import com.autoexpert.app.data.remote.model.*
import com.autoexpert.app.util.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val api: SupabaseApi,
    private val session: SessionManager,
    private val saleDao: SaleEntryQueueDao,
    private val attendanceDao: AttendanceQueueDao,
    private val skuDao: SkuDao,
    private val vehicleTypeDao: VehicleTypeDao,
    private val competitorBrandDao: CompetitorBrandDao,
    private val commissionPackageDao: CommissionPackageDao,
    private val commissionTierDao: CommissionTierDao,
    private val baCommissionOverrideDao: BaCommissionOverrideDao,
    private val noticeDao: NoticeDao,
    private val messageDao: MessageDao,
    private val payoutDao: PayoutDao,
    private val leaveDao: LeaveRequestDao,
    private val targetDao: TargetDao,
    private val gson: Gson,
) : CoroutineWorker(ctx, params) {

    private val apiKey     = BuildConfig.SUPABASE_ANON_KEY
    private val authHeader = "Bearer $apiKey"

    override suspend fun doWork(): Result {
        return try {
            syncReferenceData()
            syncSaleEntries()
            syncAttendance()
            syncUserData()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private suspend fun syncReferenceData() {
        api.getSkus(apiKey = apiKey, auth = authHeader).body()?.let { skuList ->
            skuDao.upsertAll(skuList.map { sku ->
                SkuEntity(
                    id            = sku.id,
                    name          = sku.name,
                    productType   = sku.productType,
                    volumeMl      = sku.volumeMl,
                    purchasePrice = sku.purchasePrice,
                    marginPercent = sku.marginPercent,
                    sellingPrice  = sku.sellingPrice,
                    isActive      = sku.isActive
                )
            })
        }

        api.getVehicleTypes(apiKey = apiKey, auth = authHeader).body()?.let { vtList ->
            vehicleTypeDao.upsertAll(vtList.map { vt ->
                VehicleTypeEntity(
                    id        = vt.id,
                    name      = vt.name,
                    iconKey   = vt.iconKey,
                    sortOrder = vt.sortOrder
                )
            })
        }

        api.getCompetitorBrands(apiKey = apiKey, auth = authHeader).body()?.let { brandList ->
            competitorBrandDao.upsertAll(brandList.map { brand ->
                CompetitorBrandEntity(
                    id   = brand.id,
                    name = brand.name
                )
            })
        }

        api.getCommissionPackages(apiKey = apiKey, auth = authHeader).body()?.let { pkgList ->
            commissionPackageDao.upsertAll(pkgList.map { pkg ->
                CommissionPackageEntity(
                    id                 = pkg.id,
                    name               = pkg.name,
                    basis              = pkg.basis,
                    minThresholdLitres = pkg.minThresholdLitres,
                    flatRate           = pkg.flatRate,
                    isGlobal           = pkg.isGlobal,
                    isActive           = pkg.isActive
                )
            })
        }

        api.getCommissionTiers(apiKey = apiKey, auth = authHeader).body()?.let { tierList ->
            commissionTierDao.upsertAll(tierList.map { tier ->
                CommissionTierEntity(
                    id        = tier.id,
                    packageId = tier.packageId,
                    minQty    = tier.minQty,
                    maxQty    = tier.maxQty,
                    rate      = tier.rate,
                    sortOrder = tier.sortOrder
                )
            })
        }

        api.getTargets(apiKey = apiKey, auth = authHeader).body()?.let { targetList ->
            targetDao.upsertAll(targetList.map { tgt ->
                TargetEntity(
                    id            = tgt.id,
                    baId          = tgt.baId,
                    stationId     = tgt.stationId,
                    period        = tgt.period,
                    targetValue   = tgt.targetValue,
                    targetBasis   = tgt.targetBasis,
                    effectiveFrom = tgt.effectiveFrom,
                    effectiveTo   = tgt.effectiveTo
                )
            })
        }
    }

    private suspend fun syncUserData() {
        val baId = session.baId.first() ?: return

        api.getCommissionOverrides(
            baId   = "eq.$baId",
            apiKey = apiKey,
            auth   = authHeader
        ).body()?.let { overrideList ->
            baCommissionOverrideDao.upsertAll(overrideList.map { ovr ->
                BaCommissionOverrideEntity(
                    id            = ovr.id,
                    baId          = ovr.baId,
                    packageId     = ovr.packageId,
                    effectiveFrom = ovr.effectiveFrom,
                    effectiveTo   = ovr.effectiveTo
                )
            })
        }

        api.getNotices(apiKey = apiKey, auth = authHeader).body()?.let { noticeList ->
            noticeDao.upsertAll(noticeList.map { notice ->
                NoticeEntity(
                    id          = notice.id,
                    message     = notice.message,
                    targetBaIds = notice.targetBaIds,
                    isActive    = notice.isActive,
                    postedAt    = try { Instant.parse(notice.postedAt).toEpochMilli() } catch (e: Exception) { System.currentTimeMillis() }
                )
            })
        }

        api.getMessages(
            filter = "sender_id.eq.$baId,receiver_id.eq.$baId",
            apiKey = apiKey,
            auth   = authHeader
        ).body()?.let { msgList ->
            messageDao.upsertAll(msgList.map { msg ->
                MessageEntity(
                    id         = msg.id,
                    senderId   = msg.senderId,
                    senderName = msg.senderName,
                    receiverId = msg.receiverId,
                    body       = msg.body,
                    createdAt  = try { Instant.parse(msg.createdAt).toEpochMilli() } catch (e: Exception) { System.currentTimeMillis() },
                    isRead     = msg.isRead,
                    isOutgoing = msg.senderId == baId
                )
            })
        }

        api.getPayouts(baId = "eq.$baId", apiKey = apiKey, auth = authHeader).body()?.let { payoutList ->
            payoutDao.upsertAll(payoutList.map { payout ->
                PayoutEntity(
                    id         = payout.id,
                    baId       = payout.baId,
                    payoutDate = payout.payoutDate,
                    amount     = payout.amount,
                    note       = payout.note,
                    createdAt  = try { Instant.parse(payout.createdAt ?: "").toEpochMilli() } catch (e: Exception) { 0L }
                )
            })
        }

        api.getLeaveRequests(baId = "eq.$baId", apiKey = apiKey, auth = authHeader).body()?.let { leaveList ->
            leaveDao.upsertAll(leaveList.map { leave ->
                LeaveRequestEntity(
                    id        = leave.id,
                    baId      = leave.baId,
                    leaveType = leave.leaveType,
                    fromDate  = leave.fromDate,
                    toDate    = leave.toDate,
                    totalDays = leave.totalDays,
                    reason    = leave.reason,
                    status    = leave.status,
                    createdAt = try { Instant.parse(leave.createdAt).toEpochMilli() } catch (e: Exception) { System.currentTimeMillis() }
                )
            })
        }
    }

    private suspend fun syncSaleEntries() {
        val pending = saleDao.getPending()
        for (entry in pending) {
            try {
                val remote = RemoteSaleEntry(
                    baId              = entry.baId,
                    stationId         = entry.stationId,
                    customerName      = entry.customerName,
                    customerMobile    = entry.customerMobile,
                    plateNumber       = entry.plateNumber,
                    vehicleTypeId     = entry.vehicleTypeId,
                    isRepeat          = entry.isRepeat,
                    competitorBrandId = entry.competitorBrandId,
                    entryTime         = entry.entryTime,
                    syncedAt          = Instant.now().toString(),
                )
                val resp = api.postSaleEntry(remote, apiKey, authHeader)
                if (resp.isSuccessful) {
                    val remoteId = resp.body()?.firstOrNull()?.id
                    if (entry.itemsJson != "[]" && remoteId != null) {
                        val itemType = object : TypeToken<List<Map<String, Any>>>() {}.type
                        val items = gson.fromJson<List<Map<String, Any>>>(entry.itemsJson, itemType)
                        val remoteItems = items.map { item ->
                            RemoteSaleEntryItem(
                                entryId                = remoteId,
                                skuId                  = item["skuId"] as String,
                                qtyLitres              = (item["qtyLitres"] as Number).toDouble(),
                                hasApplicator          = (item["hasApplicator"] as? Boolean) ?: false,
                                applicatorQty          = (item["applicatorQty"] as? Number)?.toDouble(),
                                purchasePriceSnapshot  = (item["purchasePriceSnapshot"] as Number).toDouble(),
                                sellingPriceSnapshot   = (item["sellingPriceSnapshot"] as Number).toDouble(),
                                marginSnapshot         = (item["marginSnapshot"] as Number).toDouble(),
                                commissionRateSnapshot = (item["commissionRateSnapshot"] as? String) ?: "",
                                commissionEarned       = (item["commissionEarned"] as Number).toDouble(),
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
                    baId             = att.baId,
                    attendanceDate   = att.attendanceDate,
                    isPresent        = att.isPresent,
                    attendanceStatus = att.attendanceStatus,
                    method           = att.method,
                    checkInTime      = att.checkInTime,
                    geoLatitude      = att.geoLatitude,
                    geoLongitude     = att.geoLongitude,
                    note             = att.note,
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
'''

with open('app/src/main/java/com/autoexpert/app/service/SyncWorker.kt', 'w') as f:
    f.write(content)
print('SyncWorker.kt written successfully')
