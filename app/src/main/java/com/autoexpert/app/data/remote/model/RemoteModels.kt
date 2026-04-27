package com.autoexpert.app.data.remote.model

import com.google.gson.annotations.SerializedName

// ── Remote DTOs ───────────────────────────────────────────────────────────

data class RemoteBa(
    val id: String,
    val name: String,
    val mobile: String?,
    val cnic: String?,
    @SerializedName("station_id") val stationId: String?,
    @SerializedName("app_pin") val appPin: String,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("employment_type") val employmentType: String?,
    @SerializedName("current_monthly_salary") val currentMonthlySalary: Double = 0.0,
    @SerializedName("joined_at") val joinedAt: String?,
    @SerializedName("leave_annual_limit") val leaveAnnualLimit: Int = 0,
    @SerializedName("leave_casual_limit") val leaveCasualLimit: Int = 0,
    @SerializedName("leave_sick_limit") val leaveSickLimit: Int = 0,
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
    @SerializedName("product_type") val productType: String?,
    @SerializedName("volume_ml") val volumeMl: Double,
    @SerializedName("purchase_price") val purchasePrice: Double,
    @SerializedName("margin_percent") val marginPercent: Double,
    @SerializedName("selling_price") val sellingPrice: Double,
    @SerializedName("is_active") val isActive: Boolean = true,
)

data class RemoteVehicleType(
    val id: String,
    val name: String,
    @SerializedName("icon_key") val iconKey: String,
    @SerializedName("sort_order") val sortOrder: Int = 0,
)

data class RemoteCompetitorBrand(
    val id: String,
    val name: String,
)

data class RemoteCommissionPackage(
    val id: String,
    val name: String,
    val basis: String,
    @SerializedName("min_threshold_litres") val minThresholdLitres: Double = 0.0,
    @SerializedName("flat_rate") val flatRate: Double = 0.0,
    @SerializedName("is_global") val isGlobal: Boolean = false,
    @SerializedName("is_active") val isActive: Boolean = true,
    val tiers: List<RemoteCommissionTier> = emptyList(),
)

data class RemoteCommissionTier(
    val id: String,
    @SerializedName("package_id") val packageId: String,
    @SerializedName("min_qty") val minQty: Double,
    @SerializedName("max_qty") val maxQty: Double?,
    val rate: Double,
    @SerializedName("sort_order") val sortOrder: Int = 0,
)

data class RemoteBaCommissionOverride(
    val id: String,
    @SerializedName("ba_id") val baId: String,
    @SerializedName("package_id") val packageId: String,
    @SerializedName("effective_from") val effectiveFrom: String,
    @SerializedName("effective_to") val effectiveTo: String?,
)

data class RemoteSaleEntry(
    val id: String? = null,
    @SerializedName("serial_number") val serialNumber: String? = null,
    @SerializedName("ba_id") val baId: String,
    @SerializedName("station_id") val stationId: String,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("customer_mobile") val customerMobile: String? = null,
    @SerializedName("plate_number") val plateNumber: String?,
    @SerializedName("vehicle_type_id") val vehicleTypeId: String?,
    @SerializedName("competitor_brand_id") val competitorBrandId: String?,
    @SerializedName("is_repeat") val isRepeat: Boolean,
    @SerializedName("entry_time") val entryTime: String,
    @SerializedName("synced_at") val syncedAt: String? = null,
    @SerializedName("sale_entry_items") val items: List<RemoteSaleEntryItem>? = null,
)

data class RemoteSaleEntryItem(
    @SerializedName("entry_id") val entryId: String,
    @SerializedName("sku_id") val skuId: String,
    @SerializedName("qty_litres") val qtyLitres: Double,
    @SerializedName("has_applicator") val hasApplicator: Boolean = false,
    @SerializedName("applicator_qty") val applicatorQty: Double? = null,
    @SerializedName("purchase_price_snapshot") val purchasePriceSnapshot: Double,
    @SerializedName("selling_price_snapshot") val sellingPriceSnapshot: Double,
    @SerializedName("margin_snapshot") val marginSnapshot: Double,
    @SerializedName("commission_rate_snapshot") val commissionRateSnapshot: String = "",
    @SerializedName("commission_earned") val commissionEarned: Double,
)

data class RemoteAttendance(
    val id: String? = null,
    @SerializedName("ba_id") val baId: String,
    @SerializedName("attendance_date") val attendanceDate: String,
    @SerializedName("is_present") val isPresent: Boolean,
    @SerializedName("attendance_status") val attendanceStatus: String? = null,
    val method: String = "manual",
    @SerializedName("check_in_time") val checkInTime: String? = null,
    @SerializedName("geo_latitude") val geoLatitude: Double? = null,
    @SerializedName("geo_longitude") val geoLongitude: Double? = null,
    val note: String? = null,
)

data class RemoteNotice(
    val id: String,
    val message: String,
    @SerializedName("target_ba_ids") val targetBaIds: List<String>? = null,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("posted_at") val postedAt: String,
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
    @SerializedName("payout_date") val payoutDate: String,
    val amount: Double,
    val note: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
)

data class RemoteLeaveRequest(
    val id: String,
    @SerializedName("ba_id") val baId: String,
    @SerializedName("leave_type") val leaveType: String,
    @SerializedName("from_date") val fromDate: String,
    @SerializedName("to_date") val toDate: String,
    @SerializedName("total_days") val totalDays: Int = 1,
    val reason: String?,
    val status: String,
    @SerializedName("created_at") val createdAt: String,
)

data class RemoteTarget(
    val id: String,
    @SerializedName("ba_id") val baId: String?,
    @SerializedName("station_id") val stationId: String?,
    val period: String,
    @SerializedName("target_value") val targetValue: Double,
    @SerializedName("target_basis") val targetBasis: String,
    @SerializedName("effective_from") val effectiveFrom: String,
    @SerializedName("effective_to") val effectiveTo: String?,
)
