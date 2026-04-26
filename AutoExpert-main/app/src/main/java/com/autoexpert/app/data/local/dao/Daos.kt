package com.autoexpert.app.data.local.dao

import androidx.room.*
import com.autoexpert.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

// ── Brand Ambassador ──────────────────────────────────────────────────────
@Dao
interface BrandAmbassadorDao {
    @Query("SELECT * FROM brand_ambassadors WHERE id = :id")
    suspend fun getById(id: String): BrandAmbassadorEntity?

    @Query("SELECT * FROM brand_ambassadors WHERE appPin = :pin AND isActive = 1 LIMIT 1")
    suspend fun getByPin(pin: String): BrandAmbassadorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<BrandAmbassadorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: BrandAmbassadorEntity)
}

// ── SKU ───────────────────────────────────────────────────────────────────
@Dao
interface SkuDao {
    @Query("SELECT * FROM skus WHERE isActive = 1 ORDER BY name")
    fun getAllActive(): Flow<List<SkuEntity>>

    @Query("SELECT * FROM skus WHERE isActive = 1 ORDER BY name")
    suspend fun getAllActiveOnce(): List<SkuEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<SkuEntity>)
}

// ── Vehicle Type ──────────────────────────────────────────────────────────
@Dao
interface VehicleTypeDao {
    @Query("SELECT * FROM vehicle_types ORDER BY sortOrder, name")
    fun getAll(): Flow<List<VehicleTypeEntity>>

    @Query("SELECT * FROM vehicle_types ORDER BY sortOrder, name")
    suspend fun getAllOnce(): List<VehicleTypeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<VehicleTypeEntity>)
}

// ── Competitor Brand ──────────────────────────────────────────────────────
@Dao
interface CompetitorBrandDao {
    @Query("SELECT * FROM competitor_brands ORDER BY name")
    suspend fun getAllOnce(): List<CompetitorBrandEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CompetitorBrandEntity>)
}

// ── Commission ────────────────────────────────────────────────────────────
@Dao
interface CommissionPackageDao {
    @Query("SELECT * FROM commission_packages WHERE isActive = 1")
    suspend fun getAllActive(): List<CommissionPackageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CommissionPackageEntity>)
}

@Dao
interface CommissionTierDao {
    @Query("SELECT * FROM commission_tiers WHERE packageId = :packageId ORDER BY sortOrder")
    suspend fun getByPackage(packageId: String): List<CommissionTierEntity>

    @Query("SELECT * FROM commission_tiers ORDER BY sortOrder")
    suspend fun getAll(): List<CommissionTierEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CommissionTierEntity>)
}

@Dao
interface BaCommissionOverrideDao {
    @Query("""
        SELECT * FROM ba_commission_overrides
        WHERE baId = :baId
        AND effectiveFrom <= :today
        AND (effectiveTo IS NULL OR effectiveTo >= :today)
        ORDER BY effectiveFrom DESC
        LIMIT 1
    """)
    suspend fun getActiveOverride(baId: String, today: String): BaCommissionOverrideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<BaCommissionOverrideEntity>)
}

// ── Sale Entry Queue ──────────────────────────────────────────────────────
@Dao
interface SaleEntryQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SaleEntryQueueEntity)

    @Query("SELECT * FROM sale_entries_queue WHERE syncStatus = 'pending' ORDER BY createdAt ASC")
    suspend fun getPending(): List<SaleEntryQueueEntity>

    @Query("SELECT * FROM sale_entries_queue WHERE baId = :baId ORDER BY createdAt DESC LIMIT 50")
    fun getByBa(baId: String): Flow<List<SaleEntryQueueEntity>>

    @Query("SELECT * FROM sale_entries_queue WHERE baId = :baId AND entryTime LIKE :datePrefix || '%' ORDER BY createdAt DESC")
    fun getByBaAndDate(baId: String, datePrefix: String): Flow<List<SaleEntryQueueEntity>>

    @Query("UPDATE sale_entries_queue SET syncStatus = :status, remoteId = :remoteId WHERE localId = :localId")
    suspend fun updateSyncStatus(localId: String, status: String, remoteId: String? = null)

    @Query("SELECT COUNT(*) FROM sale_entries_queue WHERE baId = :baId AND entryTime LIKE :datePrefix || '%' AND syncStatus != 'failed'")
    suspend fun countTodayEntries(baId: String, datePrefix: String): Int

    @Query("SELECT SUM(totalLitres) FROM sale_entries_queue WHERE baId = :baId AND entryTime LIKE :datePrefix || '%' AND syncStatus != 'failed'")
    suspend fun sumTodayLitres(baId: String, datePrefix: String): Double?

    @Query("SELECT SUM(totalCommission) FROM sale_entries_queue WHERE baId = :baId AND entryTime LIKE :datePrefix || '%' AND syncStatus != 'failed'")
    suspend fun sumTodayCommission(baId: String, datePrefix: String): Double?
}

// ── Attendance Queue ──────────────────────────────────────────────────────
@Dao
interface AttendanceQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AttendanceQueueEntity)

    @Query("SELECT * FROM attendance_queue WHERE syncStatus = 'pending'")
    suspend fun getPending(): List<AttendanceQueueEntity>

    @Query("SELECT * FROM attendance_queue WHERE baId = :baId ORDER BY attendanceDate DESC LIMIT 30")
    fun getByBa(baId: String): Flow<List<AttendanceQueueEntity>>

    @Query("SELECT * FROM attendance_queue WHERE baId = :baId AND attendanceDate = :date LIMIT 1")
    suspend fun getByBaAndDate(baId: String, date: String): AttendanceQueueEntity?

    @Query("UPDATE attendance_queue SET syncStatus = :status, remoteId = :remoteId WHERE localId = :localId")
    suspend fun updateSyncStatus(localId: String, status: String, remoteId: String? = null)

    @Query("SELECT COUNT(*) FROM attendance_queue WHERE baId = :baId AND isPresent = 1 AND attendanceDate LIKE :monthPrefix || '%'")
    suspend fun countPresentDays(baId: String, monthPrefix: String): Int
}

// ── Notice ────────────────────────────────────────────────────────────────
@Dao
interface NoticeDao {
    @Query("SELECT * FROM notices WHERE isActive = 1 ORDER BY postedAt DESC")
    fun getAll(): Flow<List<NoticeEntity>>

    @Query("SELECT COUNT(*) FROM notices WHERE isActive = 1 AND isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<NoticeEntity>)

    @Query("UPDATE notices SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: String)

    @Query("UPDATE notices SET isRead = 1")
    suspend fun markAllRead()
}

// ── Message ───────────────────────────────────────────────────────────────
@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY createdAt ASC")
    fun getAll(): Flow<List<MessageEntity>>

    @Query("SELECT COUNT(*) FROM messages WHERE isRead = 0 AND isOutgoing = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MessageEntity)

    @Query("UPDATE messages SET isRead = 1 WHERE receiverId = :baId")
    suspend fun markAllRead(baId: String)
}

// ── Payout ────────────────────────────────────────────────────────────────
@Dao
interface PayoutDao {
    @Query("SELECT * FROM payouts WHERE baId = :baId ORDER BY payoutDate DESC")
    fun getByBa(baId: String): Flow<List<PayoutEntity>>

    @Query("SELECT SUM(amount) FROM payouts WHERE baId = :baId")
    suspend fun getTotalPaid(baId: String): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<PayoutEntity>)
}

// ── Leave Request ─────────────────────────────────────────────────────────
@Dao
interface LeaveRequestDao {
    @Query("SELECT * FROM leave_requests WHERE baId = :baId ORDER BY fromDate DESC")
    fun getByBa(baId: String): Flow<List<LeaveRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<LeaveRequestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LeaveRequestEntity)
}

// ── Target ────────────────────────────────────────────────────────────────
@Dao
interface TargetDao {
    @Query("""
        SELECT * FROM targets
        WHERE (baId = :baId OR stationId = :stationId OR (baId IS NULL AND stationId IS NULL))
        AND period = 'daily'
        AND effectiveFrom <= :today
        AND (effectiveTo IS NULL OR effectiveTo >= :today)
        ORDER BY CASE WHEN baId IS NOT NULL THEN 0 WHEN stationId IS NOT NULL THEN 1 ELSE 2 END
        LIMIT 1
    """)
    suspend fun getActiveTarget(baId: String, stationId: String, today: String): TargetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<TargetEntity>)
}
