package com.autoexpert.app.data.remote.api

import com.autoexpert.app.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface SupabaseApi {

    // ── Auth / BAs ────────────────────────────────────────────────────────
    @GET("brand_ambassadors")
    suspend fun getBas(
        @Query("select") select: String = "id,name,email,phone,station_id,pin,status,salary_amount,city",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteBa>>

    @GET("brand_ambassadors")
    suspend fun getBaByPin(
        @Query("pin") pin: String,
        @Query("status") status: String = "eq.active",
        @Query("select") select: String = "id,name,email,phone,station_id,pin,status,salary_amount,city",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteBa>>

    // ── Stations ──────────────────────────────────────────────────────────
    @GET("stations")
    suspend fun getStations(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteStation>>

    // ── SKUs ──────────────────────────────────────────────────────────────
    @GET("skus")
    suspend fun getSkus(
        @Query("is_active") isActive: String = "eq.true",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteSku>>

    // ── Vehicle Types ─────────────────────────────────────────────────────
    @GET("vehicle_types")
    suspend fun getVehicleTypes(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteVehicleType>>

    // ── Competitor Brands ─────────────────────────────────────────────────
    @GET("competitor_brands")
    suspend fun getCompetitorBrands(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteCompetitorBrand>>

    // ── Sale Entries ──────────────────────────────────────────────────────
    @POST("sale_entries")
    @Headers("Prefer: return=representation")
    suspend fun postSaleEntry(
        @Body body: RemoteSaleEntry,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteSaleEntry>>

    @POST("sale_entry_items")
    suspend fun postSaleEntryItems(
        @Body items: List<RemoteSaleEntryItem>,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<Unit>

    @GET("sale_entries")
    suspend fun getSaleEntries(
        @Query("ba_id") baId: String,
        @Query("entry_date") date: String,
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
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteAttendance>>

    // ── Notices ───────────────────────────────────────────────────────────
    @GET("notices")
    suspend fun getNotices(
        @Query("is_active") isActive: String = "eq.true",
        @Query("order") order: String = "created_at.desc",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteNotice>>

    // ── Messages ──────────────────────────────────────────────────────────
    @GET("messages")
    suspend fun getMessages(
        @Query("or") filter: String,
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
        @Query("ba_id") baId: String,
        @Query("order") order: String = "earned_date.desc",
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemotePayout>>

    // ── Leave Requests ────────────────────────────────────────────────────
    @GET("leave_requests")
    suspend fun getLeaveRequests(
        @Query("ba_id") baId: String,
        @Query("order") order: String = "created_at.desc",
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
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
    ): Response<List<RemoteTarget>>
}
