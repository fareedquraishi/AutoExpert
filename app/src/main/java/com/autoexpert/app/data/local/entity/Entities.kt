package com.autoexpert.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ── Brand Ambassador ─────────────────────────────────────────────────────
@Entity(tableName = "brand_ambassadors")
data class BrandAmbassadorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val cnic: String?,
    val stationId: String?,
    val stationName: String?,
    val appPin: String,
    val isActive: Boolean = true,
    val employmentType: String?,
    val currentMonthlySalary: Double = 0.0,
    val joinedAt: String?,
    val leaveAnnualLimit: Int = 0,
    val leaveCasualLimit: Int = 0,
    val leaveSickLimit: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

// ── SKU / Product ─────────────────────────────────────────────────────────
@Entity(tableName = "skus")
data class SkuEntity(
    @PrimaryKey val id: String,
    val name: String,
    val productType: String?,
    val volumeMl: Double,           // e.g. 1000 = 1L, 4000 = 4L
    val purchasePrice: Double,
    val marginPercent: Double,
    val sellingPrice: Double,
    val isActive: Boolean = true
) {
    // Convenience: litres derived from ml
    val volumeLitres: Double get() = volumeMl / 1000.0
}

// ── Vehicle Types ─────────────────────────────────────────────────────────
@Entity(tableName = "vehicle_types")
data class VehicleTypeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconKey: String,            // "car" | "motorcycle" | "van" | "truck" etc.
    val sortOrder: Int = 0
)

// ── Competitor Brands ─────────────────────────────────────────────────────
@Entity(tableName = "competitor_brands")
data class CompetitorBrandEntity(
    @PrimaryKey val id: String,
    val name: String
)

// ── Commission Package ────────────────────────────────────────────────────
@Entity(tableName = "commission_packages")
data class CommissionPackageEntity(
    @PrimaryKey val id: String,
    val name: String,
    val basis: String,              // "tiered" | "flat" | "per_litre"
    val minThresholdLitres: Double = 0.0,
    val flatRate: Double = 0.0,
    val isGlobal: Boolean = false,
    val isActive: Boolean = true
)

// ── Commission Tier ───────────────────────────────────────────────────────
@Entity(tableName = "commission_tiers")
data class CommissionTierEntity(
    @PrimaryKey val id: String,
    val packageId: String,
    val minQty: Double,
    val maxQty: Double?,
    val rate: Double,
    val sortOrder: Int = 0
)

// ── BA Commission Override ────────────────────────────────────────────────
@Entity(tableName = "ba_commission_overrides")
data class BaCommissionOverrideEntity(
    @PrimaryKey val id: String,
    val baId: String,
    val packageId: String,
    val effectiveFrom: String,
    val effectiveTo: String?
)

// ── Sale Entry (Offline Queue) ────────────────────────────────────────────
@Entity(tableName = "sale_entries_queue")
data class SaleEntryQueueEntity(
    @PrimaryKey val localId: String,
    val remoteId: String? = null,
    val serialNumber: String? = null,
    val baId: String,
    val stationId: String,
    val customerName: String,
    val customerMobile: String?,
    val plateNumber: String?,
    val vehicleTypeId: String?,
    val vehicleTypeName: String?,
    val isRepeat: Boolean = false,
    val competitorBrandId: String? = null,
    val isApplicator: Boolean = false,
    val applicatorSkuId: String? = null,          // true if any item has applicator
    val notes: String? = null,
    val totalLitres: Double = 0.0,
    val totalCommission: Double = 0.0,
    val entryTime: String,                      // ISO timestamp, not just date
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "pending",         // pending | synced | failed
    val itemsJson: String = "[]"
)

// ── Attendance ────────────────────────────────────────────────────────────
@Entity(tableName = "attendance_queue")
data class AttendanceQueueEntity(
    @PrimaryKey val localId: String,
    val remoteId: String? = null,
    val baId: String,
    val attendanceDate: String,
    val isPresent: Boolean,
    val attendanceStatus: String? = null,       // present|absent|leave|sick|half_leave
    val method: String = "manual",              // "gps" | "manual"
    val checkInTime: String? = null,
    val geoLatitude: Double? = null,
    val geoLongitude: Double? = null,
    val note: String? = null,
    val syncStatus: String = "pending"
)

// ── Notice ─────────────────────────────────────────────────────────────────
@Entity(tableName = "notices")
data class NoticeEntity(
    @PrimaryKey val id: String,
    val message: String,                        // single field matching backend
    val targetBaIds: String?,
    val isActive: Boolean = true,
    val postedAt: Long,
    val isRead: Boolean = false
)

// ── Chat Message ──────────────────────────────────────────────────────────
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val body: String,
    val createdAt: Long,
    val isRead: Boolean = false,
    val isOutgoing: Boolean = false
)

// ── Payout / Wallet ───────────────────────────────────────────────────────
@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey val id: String,
    val baId: String,
    val payoutDate: String,
    val amount: Double,
    val note: String? = null,
    val createdAt: Long = 0L
)

// ── Leave Request ─────────────────────────────────────────────────────────
@Entity(tableName = "leave_requests")
data class LeaveRequestEntity(
    @PrimaryKey val id: String,
    val baId: String,
    val leaveType: String,
    val fromDate: String,
    val toDate: String,
    val totalDays: Int = 1,
    val reason: String?,
    val status: String,             // pending | approved | rejected
    val createdAt: Long
)

// ── Target ────────────────────────────────────────────────────────────────
@Entity(tableName = "targets")
data class TargetEntity(
    @PrimaryKey val id: String,
    val baId: String?,
    val stationId: String?,
    val period: String,             // daily | weekly | monthly
    val targetValue: Double,
    val targetBasis: String,        // litres | reach | packs  — matches backend "target_basis"
    val effectiveFrom: String,
    val effectiveTo: String?
)
