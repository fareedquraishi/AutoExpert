package com.autoexpert.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.autoexpert.app.data.local.dao.*
import com.autoexpert.app.data.local.entity.*

@Database(
    entities = [
        BrandAmbassadorEntity::class,
        SkuEntity::class,
        VehicleTypeEntity::class,
        CompetitorBrandEntity::class,
        SaleEntryQueueEntity::class,
        AttendanceQueueEntity::class,
        NoticeEntity::class,
        MessageEntity::class,
        PayoutEntity::class,
        LeaveRequestEntity::class,
        TargetEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun brandAmbassadorDao(): BrandAmbassadorDao
    abstract fun skuDao(): SkuDao
    abstract fun vehicleTypeDao(): VehicleTypeDao
    abstract fun competitorBrandDao(): CompetitorBrandDao
    abstract fun saleEntryQueueDao(): SaleEntryQueueDao
    abstract fun attendanceQueueDao(): AttendanceQueueDao
    abstract fun noticeDao(): NoticeDao
    abstract fun messageDao(): MessageDao
    abstract fun payoutDao(): PayoutDao
    abstract fun leaveRequestDao(): LeaveRequestDao
    abstract fun targetDao(): TargetDao
}
