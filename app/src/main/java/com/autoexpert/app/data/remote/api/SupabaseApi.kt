package com.autoexpert.app.data.remote.api

import com.autoexpert.app.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface SupabaseApi {

    // ── Auth / BAs ────────────────────────────────────────────────────────
    @GET("brand_ambassadors")
    suspend fun getBaByPin(
        @Query("app_pin") appPin: String,           // "eq.123456"
        @Query("is_active") isActive: String = "eq.true",
        @Query("select") select: String = "id,name,mobile,cnic,station_id,app_pin,is_active,employment_type,current_monthly_salary,joined_at,leave_annual_limit,leave_casual_limit,leave_sick_limit",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteBa>>

    // ── Stations ──────────────────────────────────────────────────────────
    @GET("stations")
    suspend fun getStationById(
        @Query("id") id: String,
        @Query("select") select: String = "id,name,city,latitude,longitude,geofence_radius_m",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteStation>>

    @GET("stations")
    suspend fun getStations(
        @Query("select") select: String = "id,name,city,latitude,longitude,geofence_radius_m",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteStation>>

    // ── SKUs ──────────────────────────────────────────────────────────────
    @GET("skus")
    suspend fun getSkus(
        @Query("is_active") isActive: String = "eq.true",
        @Query("order") order: String = "name",
        @Query("select") select: String = "id,name,product_type,volume_ml,purchase_price,margin_percent,selling_price,is_active",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteSku>>

    // ── Vehicle Types ─────────────────────────────────────────────────────
    @GET("vehicle_types")
    suspend fun getVehicleTypes(
        @Query("is_active") isActive: String = "eq.true",
        @Query("order") order: String = "sort_order",
        @Query("select") select: String = "id,name,icon_key,sort_order",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteVehicleType>>

    // ── Competitor Brands ─────────────────────────────────────────────────
    @GET("competitor_brands")
    suspend fun getCompetitorBrands(
        @Query("is_active") isActive: String = "eq.true",
        @Query("order") order: String = "name",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteCompetitorBrand>>

    // ── Commission Packages ───────────────────────────────────────────────
    @GET("commission_packages")
    suspend fun getCommissionPackages(
        @Query("is_active") isActive: String = "eq.true",
        @Query("select") select: String = "id,name,basis,min_threshold_litres,flat_rate,is_global,is_active",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteCommissionPackage>>

    @GET("commission_tiers")
    suspend fun getCommissionTiers(
        @Query("select") select: String = "id,package_id,min_qty,max_qty,rate,sort_order",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteCommissionTier>>

    @GET("ba_commission_overrides")
    suspend fun getCommissionOverrides(
        @Query("ba_id") baId: String,               // "eq.<uuid>"
        @Query("order") order: String = "effective_from.desc",
        @Query("select") select: String = "id,ba_id,package_id,effective_from,effective_to",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteBaCommissionOverride>>

    // ── Sale Entries ──────────────────────────────────────────────────────
    @POST("sale_entries")
    @Headers("Prefer: return=representation")
    suspend fun postSaleEntry(
        @Body body: RemoteSaleEntry,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteSaleEntry>>

    @POST("sale_entry_items")
    @Headers("Prefer: return=representation")
    suspend fun postSaleEntryItem(
        @Body item: RemoteSaleEntryItem,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteSaleEntryItem>>

    @POST("sale_entry_items")
    suspend fun postSaleEntryItems(
        @Body items: List<RemoteSaleEntryItem>,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<Unit>

    @GET("sale_entries")
    suspend fun getSaleEntries(
        @Query("ba_id") baId: String,               // "eq.<uuid>"
        @Query("entry_time") date: String,           // "gte.2024-01-01"
        @Query("select") select: String = "*",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteSaleEntry>>

    // ── Attendance ────────────────────────────────────────────────────────
    @POST("attendance")
    @Headers("Prefer: return=representation")
    suspend fun postAttendance(
        @Body body: RemoteAttendance,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteAttendance>>

    @GET("attendance")
    suspend fun getAttendance(
        @Query("ba_id") baId: String,
        @Query("attendance_date") dateFilter: String,
        @Query("select") select: String = "id,ba_id,attendance_date,is_present,attendance_status,method,check_in_time",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteAttendance>>

    // ── Notices ───────────────────────────────────────────────────────────
    @GET("notices")
    suspend fun getNotices(
        @Query("is_active") isActive: String = "eq.true",
        @Query("order") order: String = "posted_at.desc",
        @Query("select") select: String = "id,message,target_ba_ids,is_active,posted_at",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteNotice>>

    // ── Messages ──────────────────────────────────────────────────────────
    @GET("messages")
    suspend fun getMessages(
        @Query("or") filter: String,                // "sender_id.eq.X,receiver_id.eq.X"
        @Query("order") order: String = "created_at.asc",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteMessage>>

    @POST("messages")
    @Headers("Prefer: return=representation")
    suspend fun postMessage(
        @Body body: Map<String, String>,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteMessage>>

    @PATCH("messages")
    suspend fun markMessagesRead(
        @Query("receiver_id") baId: String,
        @Query("is_read") isRead: String = "eq.false",
        @Body body: Map<String, Boolean>,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<Unit>

    // ── Payouts ───────────────────────────────────────────────────────────
    @GET("daily_payouts")
    suspend fun getPayouts(
        @Query("ba_id") baId: String,               // "eq.<uuid>"
        @Query("order") order: String = "payout_date.desc",
        @Query("select") select: String = "id,ba_id,payout_date,amount,note,created_at",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemotePayout>>

    // ── Leave Requests ────────────────────────────────────────────────────
    @GET("leave_requests")
    suspend fun getLeaveRequests(
        @Query("ba_id") baId: String,
        @Query("order") order: String = "from_date.desc",
        @Query("select") select: String = "id,ba_id,leave_type,from_date,to_date,total_days,reason,status,created_at",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteLeaveRequest>>

    @POST("leave_requests")
    @Headers("Prefer: return=representation")
    suspend fun postLeaveRequest(
        @Body body: Map<String, String>,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteLeaveRequest>>

    // ── Targets ───────────────────────────────────────────────────────────
    @GET("targets")
    suspend fun getTargets(
        @Query("select") select: String = "id,ba_id,station_id,period,target_value,target_basis,effective_from,effective_to",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteTarget>>
}
