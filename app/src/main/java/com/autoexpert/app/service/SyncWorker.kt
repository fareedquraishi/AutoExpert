package com.autoexpert.app.service

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
            session.updateLastSync()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    // ── Pull reference data — runs every sync cycle ───────────────────────
    private suspend fun syncReferenceData() {
        // SKUs
        api.getSkus(apiKey = apiKey, auth = authHeader).body()?.let { list ->
            skuDao.upsertAll(list.map { s ->
                SkuEntity(
                    id            = s.id,
                    name          = s.name,
                    productType   = s.productType,
                    volumeMl      = s.volumeMl,
                    purchasePrice = s.purchasePrice,
                    marginPercent = s.marginPercent,
                    sellingPrice  = s.sellingPrice,
                    isActive      = s.isActive
                )
            })
        }

        // Vehicle types
        api.getVehicleTypes(apiKey = apiKey, auth = authHeader).body()?.let { list ->
            vehicleTypeDao.upsertAll(list.map { v ->
                VehicleTypeEntity(id = v.id, name = v.name, iconKey = v.iconKey, sortOrder = v.sortOrder)
            })
        }

        // Competitor brands
        api.getCompetitorBrands(apiKey = apiKey, auth = authHeader).body()?.let { list ->
            competitorBrandDao.upsertAll(list.map { b ->
                CompetitorBrandEntity(id = b.id, name = b.name)
            })
        }

        // Commission packages
        api.getCommissionPackages(apiKey = apiKey, auth = authHeader).body()?.let { list ->
            commissionPackageDao.upsertAll(list.map { p ->
                CommissionPackageEntity(
                    id                  = p.id,
                    name                = p.name,
                    basis               = p.basis,
                    minThresholdLitres  = p.minThresholdLitres,
                    flatRate            = p.flatRate,
                    isGlobal            = p.isGlobal,
                    isActive            = p.isActive
                )
            })
        }

        // Commission tiers
        api.getCommissionTiers(apiKey = apiKey, auth = authHeader).body()?.let { list ->
            commissionTierDao.upsertAll(list.map { t ->
                CommissionTierEntity(
                    id         = t.id,
                    packageId  = t.packageId,
                    minQty     = t.minQty,
                    maxQty     = t.maxQty,
                    rate       = t.rate,
                    sortOrder  = t.sortOrder
                )
            })
        }

        // Targets
        api.getTargets(apiKey = apiKey, auth = authHeader).body()?.let { list ->
            targetDao.upsertAll(list.map { t ->
                TargetEntity(
                    id            = t.id,
                    baId          = t.baId,
                    stationId     = t.stationId,
                    period        = t.period,
                    targetValue   = t.targetValue,
                    targetBasis   = t.targetBasis,
                    effectiveFrom = t.effectiveFrom,
                    effectiveTo   = t.effectiveTo
                )
            })
        }
    }

    // ── Pull per-user data ────────────────────────────────────────────────
    private suspend fun syncUserData() {
        val baId = session.baId.first() ?: return

        // Commission overrides for this BA
        api.getCommissionOverrides(
            baId   = "eq.$baId",
            apiKey = apiKey,
            auth   = authHeader
        ).body()?.let { list ->
            baCommissionOverrideDao.upsertAll(list.map { o ->
                BaCommissionOverrideEntity(
                    id            = o.id,
                    baId          = o.baId,
                    packageId     = o.packageId,
                    effectiveFrom = o.effectiveFrom,
                    effectiveTo   = o.effectiveTo
                )
            })
        }

        // Notices
        api.getNotices(apiKey = apiKey, auth = authHeader).body()?.let { list ->
            noticeDao.upsertAll(list.map { n ->
                NoticeEntity(
                    id          = n.id,
                    message     = n.message,
                    targetBaIds = n.targetBaIds,
                    isActive    = n.isActive,
                    postedAt    = try { Instant.parse(n.postedAt).toEpochMilli() } catch (e: Exception) { System.currentTimeMillis() }
                )
            })
        }

        // Messages
        api.getMessages(
            filter = "sender_id.eq.$baId,receiver_id.eq.$baId",
            apiKey = apiKey, auth = authHeader
        ).body()?.let { list ->
            messageDao.upsertAll(list.map { m ->
                MessageEntity(
                    id         = m.id,
                    senderId   = m.senderId,
                    senderName = m.senderName,
                    receiverId = m.receiverId,
                    body       = m.body,
                    createdAt  = try { Instant.parse(m.createdAt).toEpochMilli() } catch (e: Exception) { System.currentTimeMillis() },
                    isRead     = m.isRead,
                    isOutgoing = m.senderId == baId
                )
            })
        }

        // Payouts
        api.getPayouts(baId = "eq.$baId", apiKey = apiKey, auth = authHeader).body()?.let { list ->
            payoutDao.upsertAll(list.map { p ->
                PayoutEntity(
                    id         = p.id,
                    baId       = p.baId,
                    payoutDate = p.payoutDate,
                    amount     = p.amount,
                    note       = p.note,
                    createdAt  = try { Instant.parse(p.createdAt ?: "").toEpochMilli() } catch (e: Exception) { 0L }
                )
            })
        }

        // Leave requests
        api.getLeaveRequests(baId = "eq.$baId", apiKey = apiKey, auth = authHeader).body()?.let { list ->
            leaveDao.upsertAll(list.map { l ->
                LeaveRequestEntity(
                    id        = l.id,
                    baId      = l.baId,
                    leaveType = l.leaveType,
                    fromDate  = l.fromDate,
                    toDate    = l.toDate,
                    totalDays = l.totalDays,
                    reason    = l.reason,
                    status    = l.status,
                    createdAt = try { Instant.parse(l.createdAt).toEpochMilli() } catch (e: Exception) { System.currentTimeMillis() }
                )
            })
        }
    }

    // ── Push pending sale entries ─────────────────────────────────────────
    private suspend fun syncSaleEntries() {
        val pending = saleDao.getPending()
        for (entry in pending) {
            try {
                val remote = RemoteSaleEntry(
                    baId             = entry.baId,
                    stationId        = entry.stationId,
                    customerName     = entry.customerName,
                    customerMobile   = entry.customerMobile,
                    plateNumber      = entry.plateNumber,
                    vehicleTypeId    = entry.vehicleTypeId,
                    isRepeat         = entry.isRepeat,
                    competitorBrandId= entry.competitorBrandId,
                    isApplicator     = entry.isApplicator,
                    applicatorSkuId  = entry.applicatorSkuId,
                    entryTime        = entry.entryTime,
                    syncedAt         = Instant.now().toString(),
                )
                val resp = api.postSaleEntry(remote, apiKey, authHeader)
                if (resp.isSuccessful) {
                    val remoteId = resp.body()?.firstOrNull()?.id
                    if (entry.itemsJson != "[]" && remoteId != null) {
                        val itemType = object : TypeToken<List<Map<String, Any>>>() {}.type
                        val items = gson.fromJson<List<Map<String, Any>>>(entry.itemsJson, itemType)
                        val remoteItems = items.map { item ->
                            RemoteSaleEntryItem(
                                entryId                 = remoteId,   // ← correct FK column
                                skuId                   = item["skuId"] as String,
                                qtyLitres               = item["qtyLitres"] as Double,
                                hasApplicator           = (item["hasApplicator"] as? Boolean) ?: false,
                                applicatorQty           = item["applicatorQty"] as? Double,
                                purchasePriceSnapshot   = item["purchasePriceSnapshot"] as Double,
                                sellingPriceSnapshot    = item["sellingPriceSnapshot"] as Double,
                                marginSnapshot          = item["marginSnapshot"] as Double,
                                commissionRateSnapshot  = (item["commissionRateSnapshot"] as? String) ?: "",
                                commissionEarned        = item["commissionEarned"] as Double,
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

    // ── Push pending attendance ───────────────────────────────────────────
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
