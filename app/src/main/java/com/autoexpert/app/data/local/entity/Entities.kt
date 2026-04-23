package com.autoexpert.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ── Brand Ambassador ─────────────────────────────────────────────────────
@Entity(tableName = "brand_ambassadors")
data class BrandAmbassadorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val stationId: String?,
    val stationName: String?,
    val pin: String,
    val status: String = "active",
    val salaryAmount: Double = 0.0,
    val city: String?,
    val updatedAt: Long = System.currentTimeMillis()
)

// ── SKU / Product ─────────────────────────────────────────────────────────
@Entity(tableName = "skus")
data class SkuEntity(
    @PrimaryKey val id: String,
    val name: String,
    val volumeLitres: Double,
    val packVolume: Double,
    val sellingPrice: Double,
    val costPrice: Double,
    val margin: Double,
    val isActive: Boolean = true
)

// ── Vehicle Types ─────────────────────────────────────────────────────────
@Entity(tableName = "vehicle_types")
data class VehicleTypeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val icon: String
)

// ── Competitor Brands ─────────────────────────────────────────────────────
@Entity(tableName = "competitor_brands")
data class CompetitorBrandEntity(
    @PrimaryKey val id: String,
    val name: String
)

// ── Sale Entry (Offline Queue) ────────────────────────────────────────────
@Entity(tableName = "sale_entries_queue")
data class SaleEntryQueueEntity(
    @PrimaryKey val localId: String,
    val remoteId: String? = null,
    val baId: String,
    val stationId: String,
    val customerName: String,
    val mobile: String?,
    val plateNumber: String?,
    val vehicleTypeId: String?,
    val vehicleTypeName: String?,
    val isRepeat: Boolean = false,
    val competitorBrandId: String? = null,
    val isApplicator: Boolean = false,
    val notes: String? = null,
    val totalLitres: Double = 0.0,
    val totalCommission: Double = 0.0,
    val entryDate: String,
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "pending", // pending | synced | failed
    val itemsJson: String = "[]"        // serialized list of SaleEntryItemEntity
)

// ── Attendance ────────────────────────────────────────────────────────────
@Entity(tableName = "attendance_queue")
data class AttendanceQueueEntity(
    @PrimaryKey val localId: String,
    val remoteId: String? = null,
    val baId: String,
    val attendanceDate: String,
    val status: String,  // P / A / H / L / S / PH
    val markedByGps: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val syncStatus: String = "pending"
)

// ── Notice ─────────────────────────────────────────────────────────────────
@Entity(tableName = "notices")
data class NoticeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val targetBaIds: String?,   // JSON array or null for all
    val isActive: Boolean = true,
    val createdAt: Long,
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
    val earnedDate: String,
    val earnedAmount: Double,
    val allocatedAmount: Double,
    val balanceAmount: Double,
    val paymentDate: String?,
    val paidAmount: Double
)

// ── Leave Request ─────────────────────────────────────────────────────────
@Entity(tableName = "leave_requests")
data class LeaveRequestEntity(
    @PrimaryKey val id: String,
    val baId: String,
    val leaveType: String,
    val fromDate: String,
    val toDate: String,
    val reason: String?,
    val status: String,   // pending | approved | rejected
    val isPaid: Boolean = true,
    val createdAt: Long
)

// ── Target ────────────────────────────────────────────────────────────────
@Entity(tableName = "targets")
data class TargetEntity(
    @PrimaryKey val id: String,
    val baId: String?,
    val stationId: String?,
    val basis: String,   // litres | reach | packs
    val targetValue: Double,
    val period: String,  // daily | weekly | monthly
    val effectiveFrom: String,
    val effectiveTo: String?
)
