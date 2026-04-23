package com.autoexpert.app.data.remote.model

import com.google.gson.annotations.SerializedName

// ── Remote DTOs ───────────────────────────────────────────────────────────

data class RemoteBa(
    val id: String,
    val name: String,
    val email: String?,
    val phone: String?,
    @SerializedName("station_id") val stationId: String?,
    val pin: String,
    val status: String = "active",
    @SerializedName("salary_amount") val salaryAmount: Double = 0.0,
    val city: String?,
)

data class RemoteStation(
    val id: String,
    val name: String,
    val city: String?,
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("geofence_radius_m") val geofenceRadius: Int = 200,
)

data class RemoteSku(
    val id: String,
    val name: String,
    @SerializedName("volume_litres") val volumeLitres: Double,
    @SerializedName("pack_volume") val packVolume: Double,
    @SerializedName("selling_price") val sellingPrice: Double,
    @SerializedName("cost_price") val costPrice: Double,
    val margin: Double,
    @SerializedName("is_active") val isActive: Boolean = true,
)

data class RemoteVehicleType(
    val id: String,
    val name: String,
    val code: String,
    val icon: String,
)

data class RemoteCompetitorBrand(
    val id: String,
    val name: String,
)

data class RemoteSaleEntry(
    val id: String? = null,
    @SerializedName("ba_id") val baId: String,
    @SerializedName("station_id") val stationId: String,
    @SerializedName("customer_name") val customerName: String,
    val mobile: String?,
    @SerializedName("plate_number") val plateNumber: String?,
    @SerializedName("vehicle_type_id") val vehicleTypeId: String?,
    @SerializedName("is_repeat") val isRepeat: Boolean,
    @SerializedName("competitor_brand_id") val competitorBrandId: String?,
    @SerializedName("is_applicator") val isApplicator: Boolean,
    val notes: String?,
    @SerializedName("total_litres") val totalLitres: Double,
    @SerializedName("total_commission") val totalCommission: Double,
    @SerializedName("entry_date") val entryDate: String,
)

data class RemoteSaleEntryItem(
    @SerializedName("sale_entry_id") val saleEntryId: String,
    @SerializedName("sku_id") val skuId: String,
    @SerializedName("qty_packs") val qtyPacks: Int,
    @SerializedName("qty_litres") val qtyLitres: Double,
    @SerializedName("unit_price") val unitPrice: Double,
    val commission: Double,
)

data class RemoteAttendance(
    val id: String? = null,
    @SerializedName("ba_id") val baId: String,
    @SerializedName("attendance_date") val attendanceDate: String,
    val status: String,
    @SerializedName("marked_by_gps") val markedByGps: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

data class RemoteNotice(
    val id: String,
    val title: String,
    val body: String,
    @SerializedName("target_ba_ids") val targetBaIds: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String,
)

data class RemoteMessage(
    val id: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("receiver_id") val receiverId: String,
    val body: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("is_read") val isRead: Boolean = false,
)

data class RemotePayout(
    val id: String,
    @SerializedName("ba_id") val baId: String,
    @SerializedName("earned_date") val earnedDate: String,
    @SerializedName("earned_amount") val earnedAmount: Double,
    @SerializedName("allocated_amount") val allocatedAmount: Double,
    @SerializedName("balance_amount") val balanceAmount: Double,
    @SerializedName("payment_date") val paymentDate: String?,
    @SerializedName("paid_amount") val paidAmount: Double,
)

data class RemoteLeaveRequest(
    val id: String,
    @SerializedName("ba_id") val baId: String,
    @SerializedName("leave_type") val leaveType: String,
    @SerializedName("from_date") val fromDate: String,
    @SerializedName("to_date") val toDate: String,
    val reason: String?,
    val status: String,
    @SerializedName("is_paid") val isPaid: Boolean,
    @SerializedName("created_at") val createdAt: String,
)

data class RemoteTarget(
    val id: String,
    @SerializedName("ba_id") val baId: String?,
    @SerializedName("station_id") val stationId: String?,
    val basis: String,
    @SerializedName("target_value") val targetValue: Double,
    val period: String,
    @SerializedName("effective_from") val effectiveFrom: String,
    @SerializedName("effective_to") val effectiveTo: String?,
)
