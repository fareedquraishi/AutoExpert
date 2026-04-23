package com.autoexpert.app.di

import android.content.Context
import androidx.room.Room
import com.autoexpert.app.BuildConfig
import com.autoexpert.app.data.local.dao.*
import com.autoexpert.app.data.local.db.AppDatabase
import com.autoexpert.app.data.remote.api.SupabaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── Database ──────────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "autoexpert.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideBaDao(db: AppDatabase) = db.brandAmbassadorDao()
    @Provides fun provideSkuDao(db: AppDatabase) = db.skuDao()
    @Provides fun provideVehicleTypeDao(db: AppDatabase) = db.vehicleTypeDao()
    @Provides fun provideCompetitorBrandDao(db: AppDatabase) = db.competitorBrandDao()
    @Provides fun provideSaleEntryQueueDao(db: AppDatabase) = db.saleEntryQueueDao()
    @Provides fun provideAttendanceQueueDao(db: AppDatabase) = db.attendanceQueueDao()
    @Provides fun provideNoticeDao(db: AppDatabase) = db.noticeDao()
    @Provides fun provideMessageDao(db: AppDatabase) = db.messageDao()
    @Provides fun providePayoutDao(db: AppDatabase) = db.payoutDao()
    @Provides fun provideLeaveRequestDao(db: AppDatabase) = db.leaveRequestDao()
    @Provides fun provideTargetDao(db: AppDatabase) = db.targetDao()

    // ── Network ───────────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    @Provides @Singleton
    fun provideRetrofit(okHttp: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("${BuildConfig.SUPABASE_URL}/rest/v1/")
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides @Singleton
    fun provideSupabaseApi(retrofit: Retrofit): SupabaseApi =
        retrofit.create(SupabaseApi::class.java)
}
