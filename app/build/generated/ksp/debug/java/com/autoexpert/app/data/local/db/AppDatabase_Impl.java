package com.autoexpert.app.data.local.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.autoexpert.app.data.local.dao.AttendanceQueueDao;
import com.autoexpert.app.data.local.dao.AttendanceQueueDao_Impl;
import com.autoexpert.app.data.local.dao.BaCommissionOverrideDao;
import com.autoexpert.app.data.local.dao.BaCommissionOverrideDao_Impl;
import com.autoexpert.app.data.local.dao.BrandAmbassadorDao;
import com.autoexpert.app.data.local.dao.BrandAmbassadorDao_Impl;
import com.autoexpert.app.data.local.dao.CommissionPackageDao;
import com.autoexpert.app.data.local.dao.CommissionPackageDao_Impl;
import com.autoexpert.app.data.local.dao.CommissionTierDao;
import com.autoexpert.app.data.local.dao.CommissionTierDao_Impl;
import com.autoexpert.app.data.local.dao.CompetitorBrandDao;
import com.autoexpert.app.data.local.dao.CompetitorBrandDao_Impl;
import com.autoexpert.app.data.local.dao.LeaveRequestDao;
import com.autoexpert.app.data.local.dao.LeaveRequestDao_Impl;
import com.autoexpert.app.data.local.dao.MessageDao;
import com.autoexpert.app.data.local.dao.MessageDao_Impl;
import com.autoexpert.app.data.local.dao.NoticeDao;
import com.autoexpert.app.data.local.dao.NoticeDao_Impl;
import com.autoexpert.app.data.local.dao.PayoutDao;
import com.autoexpert.app.data.local.dao.PayoutDao_Impl;
import com.autoexpert.app.data.local.dao.SaleEntryQueueDao;
import com.autoexpert.app.data.local.dao.SaleEntryQueueDao_Impl;
import com.autoexpert.app.data.local.dao.SkuDao;
import com.autoexpert.app.data.local.dao.SkuDao_Impl;
import com.autoexpert.app.data.local.dao.TargetDao;
import com.autoexpert.app.data.local.dao.TargetDao_Impl;
import com.autoexpert.app.data.local.dao.VehicleTypeDao;
import com.autoexpert.app.data.local.dao.VehicleTypeDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile BrandAmbassadorDao _brandAmbassadorDao;

  private volatile SkuDao _skuDao;

  private volatile VehicleTypeDao _vehicleTypeDao;

  private volatile CompetitorBrandDao _competitorBrandDao;

  private volatile CommissionPackageDao _commissionPackageDao;

  private volatile CommissionTierDao _commissionTierDao;

  private volatile BaCommissionOverrideDao _baCommissionOverrideDao;

  private volatile SaleEntryQueueDao _saleEntryQueueDao;

  private volatile AttendanceQueueDao _attendanceQueueDao;

  private volatile NoticeDao _noticeDao;

  private volatile MessageDao _messageDao;

  private volatile PayoutDao _payoutDao;

  private volatile LeaveRequestDao _leaveRequestDao;

  private volatile TargetDao _targetDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `brand_ambassadors` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `mobile` TEXT, `cnic` TEXT, `stationId` TEXT, `stationName` TEXT, `appPin` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `employmentType` TEXT, `currentMonthlySalary` REAL NOT NULL, `joinedAt` TEXT, `leaveAnnualLimit` INTEGER NOT NULL, `leaveCasualLimit` INTEGER NOT NULL, `leaveSickLimit` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `skus` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `productType` TEXT, `volumeMl` REAL NOT NULL, `purchasePrice` REAL NOT NULL, `marginPercent` REAL NOT NULL, `sellingPrice` REAL NOT NULL, `isActive` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vehicle_types` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `iconKey` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `competitor_brands` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `commission_packages` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `basis` TEXT NOT NULL, `minThresholdLitres` REAL NOT NULL, `flatRate` REAL NOT NULL, `isGlobal` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `commission_tiers` (`id` TEXT NOT NULL, `packageId` TEXT NOT NULL, `minQty` REAL NOT NULL, `maxQty` REAL, `rate` REAL NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ba_commission_overrides` (`id` TEXT NOT NULL, `baId` TEXT NOT NULL, `packageId` TEXT NOT NULL, `effectiveFrom` TEXT NOT NULL, `effectiveTo` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sale_entries_queue` (`localId` TEXT NOT NULL, `remoteId` TEXT, `serialNumber` TEXT, `baId` TEXT NOT NULL, `stationId` TEXT NOT NULL, `customerName` TEXT NOT NULL, `customerMobile` TEXT, `plateNumber` TEXT, `vehicleTypeId` TEXT, `vehicleTypeName` TEXT, `isRepeat` INTEGER NOT NULL, `competitorBrandId` TEXT, `isApplicator` INTEGER NOT NULL, `applicatorSkuId` TEXT, `notes` TEXT, `totalLitres` REAL NOT NULL, `totalCommission` REAL NOT NULL, `entryTime` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL, `itemsJson` TEXT NOT NULL, PRIMARY KEY(`localId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `attendance_queue` (`localId` TEXT NOT NULL, `remoteId` TEXT, `baId` TEXT NOT NULL, `attendanceDate` TEXT NOT NULL, `isPresent` INTEGER NOT NULL, `attendanceStatus` TEXT, `method` TEXT NOT NULL, `checkInTime` TEXT, `geoLatitude` REAL, `geoLongitude` REAL, `note` TEXT, `syncStatus` TEXT NOT NULL, PRIMARY KEY(`localId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notices` (`id` TEXT NOT NULL, `message` TEXT NOT NULL, `targetBaIds` TEXT, `isActive` INTEGER NOT NULL, `postedAt` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `messages` (`id` TEXT NOT NULL, `senderId` TEXT NOT NULL, `senderName` TEXT NOT NULL, `receiverId` TEXT NOT NULL, `body` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, `isOutgoing` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `payouts` (`id` TEXT NOT NULL, `baId` TEXT NOT NULL, `payoutDate` TEXT NOT NULL, `amount` REAL NOT NULL, `note` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `leave_requests` (`id` TEXT NOT NULL, `baId` TEXT NOT NULL, `leaveType` TEXT NOT NULL, `fromDate` TEXT NOT NULL, `toDate` TEXT NOT NULL, `totalDays` INTEGER NOT NULL, `reason` TEXT, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `targets` (`id` TEXT NOT NULL, `baId` TEXT, `stationId` TEXT, `period` TEXT NOT NULL, `targetValue` REAL NOT NULL, `targetBasis` TEXT NOT NULL, `effectiveFrom` TEXT NOT NULL, `effectiveTo` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '15bf20c4e2a337c11f7cf58c3449010b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `brand_ambassadors`");
        db.execSQL("DROP TABLE IF EXISTS `skus`");
        db.execSQL("DROP TABLE IF EXISTS `vehicle_types`");
        db.execSQL("DROP TABLE IF EXISTS `competitor_brands`");
        db.execSQL("DROP TABLE IF EXISTS `commission_packages`");
        db.execSQL("DROP TABLE IF EXISTS `commission_tiers`");
        db.execSQL("DROP TABLE IF EXISTS `ba_commission_overrides`");
        db.execSQL("DROP TABLE IF EXISTS `sale_entries_queue`");
        db.execSQL("DROP TABLE IF EXISTS `attendance_queue`");
        db.execSQL("DROP TABLE IF EXISTS `notices`");
        db.execSQL("DROP TABLE IF EXISTS `messages`");
        db.execSQL("DROP TABLE IF EXISTS `payouts`");
        db.execSQL("DROP TABLE IF EXISTS `leave_requests`");
        db.execSQL("DROP TABLE IF EXISTS `targets`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsBrandAmbassadors = new HashMap<String, TableInfo.Column>(15);
        _columnsBrandAmbassadors.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("mobile", new TableInfo.Column("mobile", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("cnic", new TableInfo.Column("cnic", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("stationId", new TableInfo.Column("stationId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("stationName", new TableInfo.Column("stationName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("appPin", new TableInfo.Column("appPin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("employmentType", new TableInfo.Column("employmentType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("currentMonthlySalary", new TableInfo.Column("currentMonthlySalary", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("joinedAt", new TableInfo.Column("joinedAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("leaveAnnualLimit", new TableInfo.Column("leaveAnnualLimit", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("leaveCasualLimit", new TableInfo.Column("leaveCasualLimit", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("leaveSickLimit", new TableInfo.Column("leaveSickLimit", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBrandAmbassadors.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBrandAmbassadors = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBrandAmbassadors = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBrandAmbassadors = new TableInfo("brand_ambassadors", _columnsBrandAmbassadors, _foreignKeysBrandAmbassadors, _indicesBrandAmbassadors);
        final TableInfo _existingBrandAmbassadors = TableInfo.read(db, "brand_ambassadors");
        if (!_infoBrandAmbassadors.equals(_existingBrandAmbassadors)) {
          return new RoomOpenHelper.ValidationResult(false, "brand_ambassadors(com.autoexpert.app.data.local.entity.BrandAmbassadorEntity).\n"
                  + " Expected:\n" + _infoBrandAmbassadors + "\n"
                  + " Found:\n" + _existingBrandAmbassadors);
        }
        final HashMap<String, TableInfo.Column> _columnsSkus = new HashMap<String, TableInfo.Column>(8);
        _columnsSkus.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkus.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkus.put("productType", new TableInfo.Column("productType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkus.put("volumeMl", new TableInfo.Column("volumeMl", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkus.put("purchasePrice", new TableInfo.Column("purchasePrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkus.put("marginPercent", new TableInfo.Column("marginPercent", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkus.put("sellingPrice", new TableInfo.Column("sellingPrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSkus.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSkus = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSkus = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSkus = new TableInfo("skus", _columnsSkus, _foreignKeysSkus, _indicesSkus);
        final TableInfo _existingSkus = TableInfo.read(db, "skus");
        if (!_infoSkus.equals(_existingSkus)) {
          return new RoomOpenHelper.ValidationResult(false, "skus(com.autoexpert.app.data.local.entity.SkuEntity).\n"
                  + " Expected:\n" + _infoSkus + "\n"
                  + " Found:\n" + _existingSkus);
        }
        final HashMap<String, TableInfo.Column> _columnsVehicleTypes = new HashMap<String, TableInfo.Column>(4);
        _columnsVehicleTypes.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleTypes.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleTypes.put("iconKey", new TableInfo.Column("iconKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicleTypes.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVehicleTypes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVehicleTypes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVehicleTypes = new TableInfo("vehicle_types", _columnsVehicleTypes, _foreignKeysVehicleTypes, _indicesVehicleTypes);
        final TableInfo _existingVehicleTypes = TableInfo.read(db, "vehicle_types");
        if (!_infoVehicleTypes.equals(_existingVehicleTypes)) {
          return new RoomOpenHelper.ValidationResult(false, "vehicle_types(com.autoexpert.app.data.local.entity.VehicleTypeEntity).\n"
                  + " Expected:\n" + _infoVehicleTypes + "\n"
                  + " Found:\n" + _existingVehicleTypes);
        }
        final HashMap<String, TableInfo.Column> _columnsCompetitorBrands = new HashMap<String, TableInfo.Column>(2);
        _columnsCompetitorBrands.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompetitorBrands.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCompetitorBrands = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCompetitorBrands = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCompetitorBrands = new TableInfo("competitor_brands", _columnsCompetitorBrands, _foreignKeysCompetitorBrands, _indicesCompetitorBrands);
        final TableInfo _existingCompetitorBrands = TableInfo.read(db, "competitor_brands");
        if (!_infoCompetitorBrands.equals(_existingCompetitorBrands)) {
          return new RoomOpenHelper.ValidationResult(false, "competitor_brands(com.autoexpert.app.data.local.entity.CompetitorBrandEntity).\n"
                  + " Expected:\n" + _infoCompetitorBrands + "\n"
                  + " Found:\n" + _existingCompetitorBrands);
        }
        final HashMap<String, TableInfo.Column> _columnsCommissionPackages = new HashMap<String, TableInfo.Column>(7);
        _columnsCommissionPackages.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionPackages.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionPackages.put("basis", new TableInfo.Column("basis", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionPackages.put("minThresholdLitres", new TableInfo.Column("minThresholdLitres", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionPackages.put("flatRate", new TableInfo.Column("flatRate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionPackages.put("isGlobal", new TableInfo.Column("isGlobal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionPackages.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCommissionPackages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCommissionPackages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCommissionPackages = new TableInfo("commission_packages", _columnsCommissionPackages, _foreignKeysCommissionPackages, _indicesCommissionPackages);
        final TableInfo _existingCommissionPackages = TableInfo.read(db, "commission_packages");
        if (!_infoCommissionPackages.equals(_existingCommissionPackages)) {
          return new RoomOpenHelper.ValidationResult(false, "commission_packages(com.autoexpert.app.data.local.entity.CommissionPackageEntity).\n"
                  + " Expected:\n" + _infoCommissionPackages + "\n"
                  + " Found:\n" + _existingCommissionPackages);
        }
        final HashMap<String, TableInfo.Column> _columnsCommissionTiers = new HashMap<String, TableInfo.Column>(6);
        _columnsCommissionTiers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionTiers.put("packageId", new TableInfo.Column("packageId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionTiers.put("minQty", new TableInfo.Column("minQty", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionTiers.put("maxQty", new TableInfo.Column("maxQty", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionTiers.put("rate", new TableInfo.Column("rate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommissionTiers.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCommissionTiers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCommissionTiers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCommissionTiers = new TableInfo("commission_tiers", _columnsCommissionTiers, _foreignKeysCommissionTiers, _indicesCommissionTiers);
        final TableInfo _existingCommissionTiers = TableInfo.read(db, "commission_tiers");
        if (!_infoCommissionTiers.equals(_existingCommissionTiers)) {
          return new RoomOpenHelper.ValidationResult(false, "commission_tiers(com.autoexpert.app.data.local.entity.CommissionTierEntity).\n"
                  + " Expected:\n" + _infoCommissionTiers + "\n"
                  + " Found:\n" + _existingCommissionTiers);
        }
        final HashMap<String, TableInfo.Column> _columnsBaCommissionOverrides = new HashMap<String, TableInfo.Column>(5);
        _columnsBaCommissionOverrides.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaCommissionOverrides.put("baId", new TableInfo.Column("baId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaCommissionOverrides.put("packageId", new TableInfo.Column("packageId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaCommissionOverrides.put("effectiveFrom", new TableInfo.Column("effectiveFrom", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaCommissionOverrides.put("effectiveTo", new TableInfo.Column("effectiveTo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBaCommissionOverrides = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBaCommissionOverrides = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBaCommissionOverrides = new TableInfo("ba_commission_overrides", _columnsBaCommissionOverrides, _foreignKeysBaCommissionOverrides, _indicesBaCommissionOverrides);
        final TableInfo _existingBaCommissionOverrides = TableInfo.read(db, "ba_commission_overrides");
        if (!_infoBaCommissionOverrides.equals(_existingBaCommissionOverrides)) {
          return new RoomOpenHelper.ValidationResult(false, "ba_commission_overrides(com.autoexpert.app.data.local.entity.BaCommissionOverrideEntity).\n"
                  + " Expected:\n" + _infoBaCommissionOverrides + "\n"
                  + " Found:\n" + _existingBaCommissionOverrides);
        }
        final HashMap<String, TableInfo.Column> _columnsSaleEntriesQueue = new HashMap<String, TableInfo.Column>(21);
        _columnsSaleEntriesQueue.put("localId", new TableInfo.Column("localId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("remoteId", new TableInfo.Column("remoteId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("serialNumber", new TableInfo.Column("serialNumber", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("baId", new TableInfo.Column("baId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("stationId", new TableInfo.Column("stationId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("customerName", new TableInfo.Column("customerName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("customerMobile", new TableInfo.Column("customerMobile", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("plateNumber", new TableInfo.Column("plateNumber", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("vehicleTypeId", new TableInfo.Column("vehicleTypeId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("vehicleTypeName", new TableInfo.Column("vehicleTypeName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("isRepeat", new TableInfo.Column("isRepeat", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("competitorBrandId", new TableInfo.Column("competitorBrandId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("isApplicator", new TableInfo.Column("isApplicator", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("applicatorSkuId", new TableInfo.Column("applicatorSkuId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("totalLitres", new TableInfo.Column("totalLitres", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("totalCommission", new TableInfo.Column("totalCommission", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("entryTime", new TableInfo.Column("entryTime", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleEntriesQueue.put("itemsJson", new TableInfo.Column("itemsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSaleEntriesQueue = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSaleEntriesQueue = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSaleEntriesQueue = new TableInfo("sale_entries_queue", _columnsSaleEntriesQueue, _foreignKeysSaleEntriesQueue, _indicesSaleEntriesQueue);
        final TableInfo _existingSaleEntriesQueue = TableInfo.read(db, "sale_entries_queue");
        if (!_infoSaleEntriesQueue.equals(_existingSaleEntriesQueue)) {
          return new RoomOpenHelper.ValidationResult(false, "sale_entries_queue(com.autoexpert.app.data.local.entity.SaleEntryQueueEntity).\n"
                  + " Expected:\n" + _infoSaleEntriesQueue + "\n"
                  + " Found:\n" + _existingSaleEntriesQueue);
        }
        final HashMap<String, TableInfo.Column> _columnsAttendanceQueue = new HashMap<String, TableInfo.Column>(12);
        _columnsAttendanceQueue.put("localId", new TableInfo.Column("localId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("remoteId", new TableInfo.Column("remoteId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("baId", new TableInfo.Column("baId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("attendanceDate", new TableInfo.Column("attendanceDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("isPresent", new TableInfo.Column("isPresent", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("attendanceStatus", new TableInfo.Column("attendanceStatus", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("method", new TableInfo.Column("method", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("checkInTime", new TableInfo.Column("checkInTime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("geoLatitude", new TableInfo.Column("geoLatitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("geoLongitude", new TableInfo.Column("geoLongitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttendanceQueue.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAttendanceQueue = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAttendanceQueue = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAttendanceQueue = new TableInfo("attendance_queue", _columnsAttendanceQueue, _foreignKeysAttendanceQueue, _indicesAttendanceQueue);
        final TableInfo _existingAttendanceQueue = TableInfo.read(db, "attendance_queue");
        if (!_infoAttendanceQueue.equals(_existingAttendanceQueue)) {
          return new RoomOpenHelper.ValidationResult(false, "attendance_queue(com.autoexpert.app.data.local.entity.AttendanceQueueEntity).\n"
                  + " Expected:\n" + _infoAttendanceQueue + "\n"
                  + " Found:\n" + _existingAttendanceQueue);
        }
        final HashMap<String, TableInfo.Column> _columnsNotices = new HashMap<String, TableInfo.Column>(6);
        _columnsNotices.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotices.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotices.put("targetBaIds", new TableInfo.Column("targetBaIds", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotices.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotices.put("postedAt", new TableInfo.Column("postedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotices.put("isRead", new TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotices = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotices = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotices = new TableInfo("notices", _columnsNotices, _foreignKeysNotices, _indicesNotices);
        final TableInfo _existingNotices = TableInfo.read(db, "notices");
        if (!_infoNotices.equals(_existingNotices)) {
          return new RoomOpenHelper.ValidationResult(false, "notices(com.autoexpert.app.data.local.entity.NoticeEntity).\n"
                  + " Expected:\n" + _infoNotices + "\n"
                  + " Found:\n" + _existingNotices);
        }
        final HashMap<String, TableInfo.Column> _columnsMessages = new HashMap<String, TableInfo.Column>(8);
        _columnsMessages.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("senderId", new TableInfo.Column("senderId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("senderName", new TableInfo.Column("senderName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("receiverId", new TableInfo.Column("receiverId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("body", new TableInfo.Column("body", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("isRead", new TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("isOutgoing", new TableInfo.Column("isOutgoing", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMessages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMessages = new TableInfo("messages", _columnsMessages, _foreignKeysMessages, _indicesMessages);
        final TableInfo _existingMessages = TableInfo.read(db, "messages");
        if (!_infoMessages.equals(_existingMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "messages(com.autoexpert.app.data.local.entity.MessageEntity).\n"
                  + " Expected:\n" + _infoMessages + "\n"
                  + " Found:\n" + _existingMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsPayouts = new HashMap<String, TableInfo.Column>(6);
        _columnsPayouts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("baId", new TableInfo.Column("baId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("payoutDate", new TableInfo.Column("payoutDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPayouts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPayouts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPayouts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPayouts = new TableInfo("payouts", _columnsPayouts, _foreignKeysPayouts, _indicesPayouts);
        final TableInfo _existingPayouts = TableInfo.read(db, "payouts");
        if (!_infoPayouts.equals(_existingPayouts)) {
          return new RoomOpenHelper.ValidationResult(false, "payouts(com.autoexpert.app.data.local.entity.PayoutEntity).\n"
                  + " Expected:\n" + _infoPayouts + "\n"
                  + " Found:\n" + _existingPayouts);
        }
        final HashMap<String, TableInfo.Column> _columnsLeaveRequests = new HashMap<String, TableInfo.Column>(9);
        _columnsLeaveRequests.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaveRequests.put("baId", new TableInfo.Column("baId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaveRequests.put("leaveType", new TableInfo.Column("leaveType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaveRequests.put("fromDate", new TableInfo.Column("fromDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaveRequests.put("toDate", new TableInfo.Column("toDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaveRequests.put("totalDays", new TableInfo.Column("totalDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaveRequests.put("reason", new TableInfo.Column("reason", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaveRequests.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaveRequests.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLeaveRequests = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLeaveRequests = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLeaveRequests = new TableInfo("leave_requests", _columnsLeaveRequests, _foreignKeysLeaveRequests, _indicesLeaveRequests);
        final TableInfo _existingLeaveRequests = TableInfo.read(db, "leave_requests");
        if (!_infoLeaveRequests.equals(_existingLeaveRequests)) {
          return new RoomOpenHelper.ValidationResult(false, "leave_requests(com.autoexpert.app.data.local.entity.LeaveRequestEntity).\n"
                  + " Expected:\n" + _infoLeaveRequests + "\n"
                  + " Found:\n" + _existingLeaveRequests);
        }
        final HashMap<String, TableInfo.Column> _columnsTargets = new HashMap<String, TableInfo.Column>(8);
        _columnsTargets.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargets.put("baId", new TableInfo.Column("baId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargets.put("stationId", new TableInfo.Column("stationId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargets.put("period", new TableInfo.Column("period", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargets.put("targetValue", new TableInfo.Column("targetValue", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargets.put("targetBasis", new TableInfo.Column("targetBasis", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargets.put("effectiveFrom", new TableInfo.Column("effectiveFrom", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargets.put("effectiveTo", new TableInfo.Column("effectiveTo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTargets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTargets = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTargets = new TableInfo("targets", _columnsTargets, _foreignKeysTargets, _indicesTargets);
        final TableInfo _existingTargets = TableInfo.read(db, "targets");
        if (!_infoTargets.equals(_existingTargets)) {
          return new RoomOpenHelper.ValidationResult(false, "targets(com.autoexpert.app.data.local.entity.TargetEntity).\n"
                  + " Expected:\n" + _infoTargets + "\n"
                  + " Found:\n" + _existingTargets);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "15bf20c4e2a337c11f7cf58c3449010b", "49e809cb027723f2878c2e0e0a07fd99");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "brand_ambassadors","skus","vehicle_types","competitor_brands","commission_packages","commission_tiers","ba_commission_overrides","sale_entries_queue","attendance_queue","notices","messages","payouts","leave_requests","targets");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `brand_ambassadors`");
      _db.execSQL("DELETE FROM `skus`");
      _db.execSQL("DELETE FROM `vehicle_types`");
      _db.execSQL("DELETE FROM `competitor_brands`");
      _db.execSQL("DELETE FROM `commission_packages`");
      _db.execSQL("DELETE FROM `commission_tiers`");
      _db.execSQL("DELETE FROM `ba_commission_overrides`");
      _db.execSQL("DELETE FROM `sale_entries_queue`");
      _db.execSQL("DELETE FROM `attendance_queue`");
      _db.execSQL("DELETE FROM `notices`");
      _db.execSQL("DELETE FROM `messages`");
      _db.execSQL("DELETE FROM `payouts`");
      _db.execSQL("DELETE FROM `leave_requests`");
      _db.execSQL("DELETE FROM `targets`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(BrandAmbassadorDao.class, BrandAmbassadorDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SkuDao.class, SkuDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VehicleTypeDao.class, VehicleTypeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CompetitorBrandDao.class, CompetitorBrandDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CommissionPackageDao.class, CommissionPackageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CommissionTierDao.class, CommissionTierDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BaCommissionOverrideDao.class, BaCommissionOverrideDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SaleEntryQueueDao.class, SaleEntryQueueDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AttendanceQueueDao.class, AttendanceQueueDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NoticeDao.class, NoticeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MessageDao.class, MessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PayoutDao.class, PayoutDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LeaveRequestDao.class, LeaveRequestDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TargetDao.class, TargetDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public BrandAmbassadorDao brandAmbassadorDao() {
    if (_brandAmbassadorDao != null) {
      return _brandAmbassadorDao;
    } else {
      synchronized(this) {
        if(_brandAmbassadorDao == null) {
          _brandAmbassadorDao = new BrandAmbassadorDao_Impl(this);
        }
        return _brandAmbassadorDao;
      }
    }
  }

  @Override
  public SkuDao skuDao() {
    if (_skuDao != null) {
      return _skuDao;
    } else {
      synchronized(this) {
        if(_skuDao == null) {
          _skuDao = new SkuDao_Impl(this);
        }
        return _skuDao;
      }
    }
  }

  @Override
  public VehicleTypeDao vehicleTypeDao() {
    if (_vehicleTypeDao != null) {
      return _vehicleTypeDao;
    } else {
      synchronized(this) {
        if(_vehicleTypeDao == null) {
          _vehicleTypeDao = new VehicleTypeDao_Impl(this);
        }
        return _vehicleTypeDao;
      }
    }
  }

  @Override
  public CompetitorBrandDao competitorBrandDao() {
    if (_competitorBrandDao != null) {
      return _competitorBrandDao;
    } else {
      synchronized(this) {
        if(_competitorBrandDao == null) {
          _competitorBrandDao = new CompetitorBrandDao_Impl(this);
        }
        return _competitorBrandDao;
      }
    }
  }

  @Override
  public CommissionPackageDao commissionPackageDao() {
    if (_commissionPackageDao != null) {
      return _commissionPackageDao;
    } else {
      synchronized(this) {
        if(_commissionPackageDao == null) {
          _commissionPackageDao = new CommissionPackageDao_Impl(this);
        }
        return _commissionPackageDao;
      }
    }
  }

  @Override
  public CommissionTierDao commissionTierDao() {
    if (_commissionTierDao != null) {
      return _commissionTierDao;
    } else {
      synchronized(this) {
        if(_commissionTierDao == null) {
          _commissionTierDao = new CommissionTierDao_Impl(this);
        }
        return _commissionTierDao;
      }
    }
  }

  @Override
  public BaCommissionOverrideDao baCommissionOverrideDao() {
    if (_baCommissionOverrideDao != null) {
      return _baCommissionOverrideDao;
    } else {
      synchronized(this) {
        if(_baCommissionOverrideDao == null) {
          _baCommissionOverrideDao = new BaCommissionOverrideDao_Impl(this);
        }
        return _baCommissionOverrideDao;
      }
    }
  }

  @Override
  public SaleEntryQueueDao saleEntryQueueDao() {
    if (_saleEntryQueueDao != null) {
      return _saleEntryQueueDao;
    } else {
      synchronized(this) {
        if(_saleEntryQueueDao == null) {
          _saleEntryQueueDao = new SaleEntryQueueDao_Impl(this);
        }
        return _saleEntryQueueDao;
      }
    }
  }

  @Override
  public AttendanceQueueDao attendanceQueueDao() {
    if (_attendanceQueueDao != null) {
      return _attendanceQueueDao;
    } else {
      synchronized(this) {
        if(_attendanceQueueDao == null) {
          _attendanceQueueDao = new AttendanceQueueDao_Impl(this);
        }
        return _attendanceQueueDao;
      }
    }
  }

  @Override
  public NoticeDao noticeDao() {
    if (_noticeDao != null) {
      return _noticeDao;
    } else {
      synchronized(this) {
        if(_noticeDao == null) {
          _noticeDao = new NoticeDao_Impl(this);
        }
        return _noticeDao;
      }
    }
  }

  @Override
  public MessageDao messageDao() {
    if (_messageDao != null) {
      return _messageDao;
    } else {
      synchronized(this) {
        if(_messageDao == null) {
          _messageDao = new MessageDao_Impl(this);
        }
        return _messageDao;
      }
    }
  }

  @Override
  public PayoutDao payoutDao() {
    if (_payoutDao != null) {
      return _payoutDao;
    } else {
      synchronized(this) {
        if(_payoutDao == null) {
          _payoutDao = new PayoutDao_Impl(this);
        }
        return _payoutDao;
      }
    }
  }

  @Override
  public LeaveRequestDao leaveRequestDao() {
    if (_leaveRequestDao != null) {
      return _leaveRequestDao;
    } else {
      synchronized(this) {
        if(_leaveRequestDao == null) {
          _leaveRequestDao = new LeaveRequestDao_Impl(this);
        }
        return _leaveRequestDao;
      }
    }
  }

  @Override
  public TargetDao targetDao() {
    if (_targetDao != null) {
      return _targetDao;
    } else {
      synchronized(this) {
        if(_targetDao == null) {
          _targetDao = new TargetDao_Impl(this);
        }
        return _targetDao;
      }
    }
  }
}
