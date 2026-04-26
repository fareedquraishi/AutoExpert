package com.autoexpert.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.autoexpert.app.data.local.entity.SaleEntryQueueEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SaleEntryQueueDao_Impl implements SaleEntryQueueDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SaleEntryQueueEntity> __insertionAdapterOfSaleEntryQueueEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSyncedByDate;

  public SaleEntryQueueDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSaleEntryQueueEntity = new EntityInsertionAdapter<SaleEntryQueueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sale_entries_queue` (`localId`,`remoteId`,`serialNumber`,`baId`,`stationId`,`customerName`,`customerMobile`,`plateNumber`,`vehicleTypeId`,`vehicleTypeName`,`isRepeat`,`competitorBrandId`,`isApplicator`,`applicatorSkuId`,`notes`,`totalLitres`,`totalCommission`,`entryTime`,`createdAt`,`syncStatus`,`itemsJson`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SaleEntryQueueEntity entity) {
        statement.bindString(1, entity.getLocalId());
        if (entity.getRemoteId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getRemoteId());
        }
        if (entity.getSerialNumber() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getSerialNumber());
        }
        statement.bindString(4, entity.getBaId());
        statement.bindString(5, entity.getStationId());
        statement.bindString(6, entity.getCustomerName());
        if (entity.getCustomerMobile() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getCustomerMobile());
        }
        if (entity.getPlateNumber() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getPlateNumber());
        }
        if (entity.getVehicleTypeId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getVehicleTypeId());
        }
        if (entity.getVehicleTypeName() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getVehicleTypeName());
        }
        final int _tmp = entity.isRepeat() ? 1 : 0;
        statement.bindLong(11, _tmp);
        if (entity.getCompetitorBrandId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getCompetitorBrandId());
        }
        final int _tmp_1 = entity.isApplicator() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        if (entity.getApplicatorSkuId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getApplicatorSkuId());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getNotes());
        }
        statement.bindDouble(16, entity.getTotalLitres());
        statement.bindDouble(17, entity.getTotalCommission());
        statement.bindString(18, entity.getEntryTime());
        statement.bindLong(19, entity.getCreatedAt());
        statement.bindString(20, entity.getSyncStatus());
        statement.bindString(21, entity.getItemsJson());
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sale_entries_queue SET syncStatus = ?, remoteId = ? WHERE localId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteSyncedByDate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sale_entries_queue WHERE baId = ? AND entryTime LIKE ? || '%' AND syncStatus = 'synced'";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SaleEntryQueueEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSaleEntryQueueEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String localId, final String status, final String remoteId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        if (remoteId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, remoteId);
        }
        _argIndex = 3;
        _stmt.bindString(_argIndex, localId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateSyncStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSyncedByDate(final String baId, final String datePrefix,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSyncedByDate.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, baId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, datePrefix);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteSyncedByDate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getPending(final Continuation<? super List<SaleEntryQueueEntity>> $completion) {
    final String _sql = "SELECT * FROM sale_entries_queue WHERE syncStatus = 'pending' ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SaleEntryQueueEntity>>() {
      @Override
      @NonNull
      public List<SaleEntryQueueEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfStationId = CursorUtil.getColumnIndexOrThrow(_cursor, "stationId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfCustomerMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "customerMobile");
          final int _cursorIndexOfPlateNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "plateNumber");
          final int _cursorIndexOfVehicleTypeId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleTypeId");
          final int _cursorIndexOfVehicleTypeName = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleTypeName");
          final int _cursorIndexOfIsRepeat = CursorUtil.getColumnIndexOrThrow(_cursor, "isRepeat");
          final int _cursorIndexOfCompetitorBrandId = CursorUtil.getColumnIndexOrThrow(_cursor, "competitorBrandId");
          final int _cursorIndexOfIsApplicator = CursorUtil.getColumnIndexOrThrow(_cursor, "isApplicator");
          final int _cursorIndexOfApplicatorSkuId = CursorUtil.getColumnIndexOrThrow(_cursor, "applicatorSkuId");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTotalLitres = CursorUtil.getColumnIndexOrThrow(_cursor, "totalLitres");
          final int _cursorIndexOfTotalCommission = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCommission");
          final int _cursorIndexOfEntryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "entryTime");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfItemsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "itemsJson");
          final List<SaleEntryQueueEntity> _result = new ArrayList<SaleEntryQueueEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SaleEntryQueueEntity _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpBaId;
            _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            final String _tmpStationId;
            _tmpStationId = _cursor.getString(_cursorIndexOfStationId);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpCustomerMobile;
            if (_cursor.isNull(_cursorIndexOfCustomerMobile)) {
              _tmpCustomerMobile = null;
            } else {
              _tmpCustomerMobile = _cursor.getString(_cursorIndexOfCustomerMobile);
            }
            final String _tmpPlateNumber;
            if (_cursor.isNull(_cursorIndexOfPlateNumber)) {
              _tmpPlateNumber = null;
            } else {
              _tmpPlateNumber = _cursor.getString(_cursorIndexOfPlateNumber);
            }
            final String _tmpVehicleTypeId;
            if (_cursor.isNull(_cursorIndexOfVehicleTypeId)) {
              _tmpVehicleTypeId = null;
            } else {
              _tmpVehicleTypeId = _cursor.getString(_cursorIndexOfVehicleTypeId);
            }
            final String _tmpVehicleTypeName;
            if (_cursor.isNull(_cursorIndexOfVehicleTypeName)) {
              _tmpVehicleTypeName = null;
            } else {
              _tmpVehicleTypeName = _cursor.getString(_cursorIndexOfVehicleTypeName);
            }
            final boolean _tmpIsRepeat;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRepeat);
            _tmpIsRepeat = _tmp != 0;
            final String _tmpCompetitorBrandId;
            if (_cursor.isNull(_cursorIndexOfCompetitorBrandId)) {
              _tmpCompetitorBrandId = null;
            } else {
              _tmpCompetitorBrandId = _cursor.getString(_cursorIndexOfCompetitorBrandId);
            }
            final boolean _tmpIsApplicator;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsApplicator);
            _tmpIsApplicator = _tmp_1 != 0;
            final String _tmpApplicatorSkuId;
            if (_cursor.isNull(_cursorIndexOfApplicatorSkuId)) {
              _tmpApplicatorSkuId = null;
            } else {
              _tmpApplicatorSkuId = _cursor.getString(_cursorIndexOfApplicatorSkuId);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final double _tmpTotalLitres;
            _tmpTotalLitres = _cursor.getDouble(_cursorIndexOfTotalLitres);
            final double _tmpTotalCommission;
            _tmpTotalCommission = _cursor.getDouble(_cursorIndexOfTotalCommission);
            final String _tmpEntryTime;
            _tmpEntryTime = _cursor.getString(_cursorIndexOfEntryTime);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final String _tmpItemsJson;
            _tmpItemsJson = _cursor.getString(_cursorIndexOfItemsJson);
            _item = new SaleEntryQueueEntity(_tmpLocalId,_tmpRemoteId,_tmpSerialNumber,_tmpBaId,_tmpStationId,_tmpCustomerName,_tmpCustomerMobile,_tmpPlateNumber,_tmpVehicleTypeId,_tmpVehicleTypeName,_tmpIsRepeat,_tmpCompetitorBrandId,_tmpIsApplicator,_tmpApplicatorSkuId,_tmpNotes,_tmpTotalLitres,_tmpTotalCommission,_tmpEntryTime,_tmpCreatedAt,_tmpSyncStatus,_tmpItemsJson);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SaleEntryQueueEntity>> getByBa(final String baId) {
    final String _sql = "SELECT * FROM sale_entries_queue WHERE baId = ? ORDER BY createdAt DESC LIMIT 50";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sale_entries_queue"}, new Callable<List<SaleEntryQueueEntity>>() {
      @Override
      @NonNull
      public List<SaleEntryQueueEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfStationId = CursorUtil.getColumnIndexOrThrow(_cursor, "stationId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfCustomerMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "customerMobile");
          final int _cursorIndexOfPlateNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "plateNumber");
          final int _cursorIndexOfVehicleTypeId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleTypeId");
          final int _cursorIndexOfVehicleTypeName = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleTypeName");
          final int _cursorIndexOfIsRepeat = CursorUtil.getColumnIndexOrThrow(_cursor, "isRepeat");
          final int _cursorIndexOfCompetitorBrandId = CursorUtil.getColumnIndexOrThrow(_cursor, "competitorBrandId");
          final int _cursorIndexOfIsApplicator = CursorUtil.getColumnIndexOrThrow(_cursor, "isApplicator");
          final int _cursorIndexOfApplicatorSkuId = CursorUtil.getColumnIndexOrThrow(_cursor, "applicatorSkuId");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTotalLitres = CursorUtil.getColumnIndexOrThrow(_cursor, "totalLitres");
          final int _cursorIndexOfTotalCommission = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCommission");
          final int _cursorIndexOfEntryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "entryTime");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfItemsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "itemsJson");
          final List<SaleEntryQueueEntity> _result = new ArrayList<SaleEntryQueueEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SaleEntryQueueEntity _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpBaId;
            _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            final String _tmpStationId;
            _tmpStationId = _cursor.getString(_cursorIndexOfStationId);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpCustomerMobile;
            if (_cursor.isNull(_cursorIndexOfCustomerMobile)) {
              _tmpCustomerMobile = null;
            } else {
              _tmpCustomerMobile = _cursor.getString(_cursorIndexOfCustomerMobile);
            }
            final String _tmpPlateNumber;
            if (_cursor.isNull(_cursorIndexOfPlateNumber)) {
              _tmpPlateNumber = null;
            } else {
              _tmpPlateNumber = _cursor.getString(_cursorIndexOfPlateNumber);
            }
            final String _tmpVehicleTypeId;
            if (_cursor.isNull(_cursorIndexOfVehicleTypeId)) {
              _tmpVehicleTypeId = null;
            } else {
              _tmpVehicleTypeId = _cursor.getString(_cursorIndexOfVehicleTypeId);
            }
            final String _tmpVehicleTypeName;
            if (_cursor.isNull(_cursorIndexOfVehicleTypeName)) {
              _tmpVehicleTypeName = null;
            } else {
              _tmpVehicleTypeName = _cursor.getString(_cursorIndexOfVehicleTypeName);
            }
            final boolean _tmpIsRepeat;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRepeat);
            _tmpIsRepeat = _tmp != 0;
            final String _tmpCompetitorBrandId;
            if (_cursor.isNull(_cursorIndexOfCompetitorBrandId)) {
              _tmpCompetitorBrandId = null;
            } else {
              _tmpCompetitorBrandId = _cursor.getString(_cursorIndexOfCompetitorBrandId);
            }
            final boolean _tmpIsApplicator;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsApplicator);
            _tmpIsApplicator = _tmp_1 != 0;
            final String _tmpApplicatorSkuId;
            if (_cursor.isNull(_cursorIndexOfApplicatorSkuId)) {
              _tmpApplicatorSkuId = null;
            } else {
              _tmpApplicatorSkuId = _cursor.getString(_cursorIndexOfApplicatorSkuId);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final double _tmpTotalLitres;
            _tmpTotalLitres = _cursor.getDouble(_cursorIndexOfTotalLitres);
            final double _tmpTotalCommission;
            _tmpTotalCommission = _cursor.getDouble(_cursorIndexOfTotalCommission);
            final String _tmpEntryTime;
            _tmpEntryTime = _cursor.getString(_cursorIndexOfEntryTime);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final String _tmpItemsJson;
            _tmpItemsJson = _cursor.getString(_cursorIndexOfItemsJson);
            _item = new SaleEntryQueueEntity(_tmpLocalId,_tmpRemoteId,_tmpSerialNumber,_tmpBaId,_tmpStationId,_tmpCustomerName,_tmpCustomerMobile,_tmpPlateNumber,_tmpVehicleTypeId,_tmpVehicleTypeName,_tmpIsRepeat,_tmpCompetitorBrandId,_tmpIsApplicator,_tmpApplicatorSkuId,_tmpNotes,_tmpTotalLitres,_tmpTotalCommission,_tmpEntryTime,_tmpCreatedAt,_tmpSyncStatus,_tmpItemsJson);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<SaleEntryQueueEntity>> getByBaAndDate(final String baId,
      final String datePrefix) {
    final String _sql = "SELECT * FROM sale_entries_queue WHERE baId = ? AND entryTime LIKE ? || '%' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    _argIndex = 2;
    _statement.bindString(_argIndex, datePrefix);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sale_entries_queue"}, new Callable<List<SaleEntryQueueEntity>>() {
      @Override
      @NonNull
      public List<SaleEntryQueueEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfStationId = CursorUtil.getColumnIndexOrThrow(_cursor, "stationId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfCustomerMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "customerMobile");
          final int _cursorIndexOfPlateNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "plateNumber");
          final int _cursorIndexOfVehicleTypeId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleTypeId");
          final int _cursorIndexOfVehicleTypeName = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleTypeName");
          final int _cursorIndexOfIsRepeat = CursorUtil.getColumnIndexOrThrow(_cursor, "isRepeat");
          final int _cursorIndexOfCompetitorBrandId = CursorUtil.getColumnIndexOrThrow(_cursor, "competitorBrandId");
          final int _cursorIndexOfIsApplicator = CursorUtil.getColumnIndexOrThrow(_cursor, "isApplicator");
          final int _cursorIndexOfApplicatorSkuId = CursorUtil.getColumnIndexOrThrow(_cursor, "applicatorSkuId");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTotalLitres = CursorUtil.getColumnIndexOrThrow(_cursor, "totalLitres");
          final int _cursorIndexOfTotalCommission = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCommission");
          final int _cursorIndexOfEntryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "entryTime");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfItemsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "itemsJson");
          final List<SaleEntryQueueEntity> _result = new ArrayList<SaleEntryQueueEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SaleEntryQueueEntity _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpBaId;
            _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            final String _tmpStationId;
            _tmpStationId = _cursor.getString(_cursorIndexOfStationId);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpCustomerMobile;
            if (_cursor.isNull(_cursorIndexOfCustomerMobile)) {
              _tmpCustomerMobile = null;
            } else {
              _tmpCustomerMobile = _cursor.getString(_cursorIndexOfCustomerMobile);
            }
            final String _tmpPlateNumber;
            if (_cursor.isNull(_cursorIndexOfPlateNumber)) {
              _tmpPlateNumber = null;
            } else {
              _tmpPlateNumber = _cursor.getString(_cursorIndexOfPlateNumber);
            }
            final String _tmpVehicleTypeId;
            if (_cursor.isNull(_cursorIndexOfVehicleTypeId)) {
              _tmpVehicleTypeId = null;
            } else {
              _tmpVehicleTypeId = _cursor.getString(_cursorIndexOfVehicleTypeId);
            }
            final String _tmpVehicleTypeName;
            if (_cursor.isNull(_cursorIndexOfVehicleTypeName)) {
              _tmpVehicleTypeName = null;
            } else {
              _tmpVehicleTypeName = _cursor.getString(_cursorIndexOfVehicleTypeName);
            }
            final boolean _tmpIsRepeat;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRepeat);
            _tmpIsRepeat = _tmp != 0;
            final String _tmpCompetitorBrandId;
            if (_cursor.isNull(_cursorIndexOfCompetitorBrandId)) {
              _tmpCompetitorBrandId = null;
            } else {
              _tmpCompetitorBrandId = _cursor.getString(_cursorIndexOfCompetitorBrandId);
            }
            final boolean _tmpIsApplicator;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsApplicator);
            _tmpIsApplicator = _tmp_1 != 0;
            final String _tmpApplicatorSkuId;
            if (_cursor.isNull(_cursorIndexOfApplicatorSkuId)) {
              _tmpApplicatorSkuId = null;
            } else {
              _tmpApplicatorSkuId = _cursor.getString(_cursorIndexOfApplicatorSkuId);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final double _tmpTotalLitres;
            _tmpTotalLitres = _cursor.getDouble(_cursorIndexOfTotalLitres);
            final double _tmpTotalCommission;
            _tmpTotalCommission = _cursor.getDouble(_cursorIndexOfTotalCommission);
            final String _tmpEntryTime;
            _tmpEntryTime = _cursor.getString(_cursorIndexOfEntryTime);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final String _tmpItemsJson;
            _tmpItemsJson = _cursor.getString(_cursorIndexOfItemsJson);
            _item = new SaleEntryQueueEntity(_tmpLocalId,_tmpRemoteId,_tmpSerialNumber,_tmpBaId,_tmpStationId,_tmpCustomerName,_tmpCustomerMobile,_tmpPlateNumber,_tmpVehicleTypeId,_tmpVehicleTypeName,_tmpIsRepeat,_tmpCompetitorBrandId,_tmpIsApplicator,_tmpApplicatorSkuId,_tmpNotes,_tmpTotalLitres,_tmpTotalCommission,_tmpEntryTime,_tmpCreatedAt,_tmpSyncStatus,_tmpItemsJson);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object countTodayEntries(final String baId, final String datePrefix,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sale_entries_queue WHERE baId = ? AND entryTime LIKE ? || '%' AND syncStatus != 'failed'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    _argIndex = 2;
    _statement.bindString(_argIndex, datePrefix);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object sumTodayLitres(final String baId, final String datePrefix,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT SUM(totalLitres) FROM sale_entries_queue WHERE baId = ? AND entryTime LIKE ? || '%' AND syncStatus != 'failed'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    _argIndex = 2;
    _statement.bindString(_argIndex, datePrefix);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object sumTodayCommission(final String baId, final String datePrefix,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT SUM(totalCommission) FROM sale_entries_queue WHERE baId = ? AND entryTime LIKE ? || '%' AND syncStatus != 'failed'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    _argIndex = 2;
    _statement.bindString(_argIndex, datePrefix);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
