package com.autoexpert.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.autoexpert.app.data.local.entity.BrandAmbassadorEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BrandAmbassadorDao_Impl implements BrandAmbassadorDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BrandAmbassadorEntity> __insertionAdapterOfBrandAmbassadorEntity;

  public BrandAmbassadorDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBrandAmbassadorEntity = new EntityInsertionAdapter<BrandAmbassadorEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `brand_ambassadors` (`id`,`name`,`mobile`,`cnic`,`stationId`,`stationName`,`appPin`,`isActive`,`employmentType`,`currentMonthlySalary`,`joinedAt`,`leaveAnnualLimit`,`leaveCasualLimit`,`leaveSickLimit`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BrandAmbassadorEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getMobile() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMobile());
        }
        if (entity.getCnic() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCnic());
        }
        if (entity.getStationId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getStationId());
        }
        if (entity.getStationName() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getStationName());
        }
        statement.bindString(7, entity.getAppPin());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getEmploymentType() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getEmploymentType());
        }
        statement.bindDouble(10, entity.getCurrentMonthlySalary());
        if (entity.getJoinedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getJoinedAt());
        }
        statement.bindLong(12, entity.getLeaveAnnualLimit());
        statement.bindLong(13, entity.getLeaveCasualLimit());
        statement.bindLong(14, entity.getLeaveSickLimit());
        statement.bindLong(15, entity.getUpdatedAt());
      }
    };
  }

  @Override
  public Object upsertAll(final List<BrandAmbassadorEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBrandAmbassadorEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final BrandAmbassadorEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBrandAmbassadorEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id,
      final Continuation<? super BrandAmbassadorEntity> $completion) {
    final String _sql = "SELECT * FROM brand_ambassadors WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BrandAmbassadorEntity>() {
      @Override
      @Nullable
      public BrandAmbassadorEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "mobile");
          final int _cursorIndexOfCnic = CursorUtil.getColumnIndexOrThrow(_cursor, "cnic");
          final int _cursorIndexOfStationId = CursorUtil.getColumnIndexOrThrow(_cursor, "stationId");
          final int _cursorIndexOfStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "stationName");
          final int _cursorIndexOfAppPin = CursorUtil.getColumnIndexOrThrow(_cursor, "appPin");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfEmploymentType = CursorUtil.getColumnIndexOrThrow(_cursor, "employmentType");
          final int _cursorIndexOfCurrentMonthlySalary = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMonthlySalary");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfLeaveAnnualLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "leaveAnnualLimit");
          final int _cursorIndexOfLeaveCasualLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "leaveCasualLimit");
          final int _cursorIndexOfLeaveSickLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "leaveSickLimit");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final BrandAmbassadorEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMobile;
            if (_cursor.isNull(_cursorIndexOfMobile)) {
              _tmpMobile = null;
            } else {
              _tmpMobile = _cursor.getString(_cursorIndexOfMobile);
            }
            final String _tmpCnic;
            if (_cursor.isNull(_cursorIndexOfCnic)) {
              _tmpCnic = null;
            } else {
              _tmpCnic = _cursor.getString(_cursorIndexOfCnic);
            }
            final String _tmpStationId;
            if (_cursor.isNull(_cursorIndexOfStationId)) {
              _tmpStationId = null;
            } else {
              _tmpStationId = _cursor.getString(_cursorIndexOfStationId);
            }
            final String _tmpStationName;
            if (_cursor.isNull(_cursorIndexOfStationName)) {
              _tmpStationName = null;
            } else {
              _tmpStationName = _cursor.getString(_cursorIndexOfStationName);
            }
            final String _tmpAppPin;
            _tmpAppPin = _cursor.getString(_cursorIndexOfAppPin);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpEmploymentType;
            if (_cursor.isNull(_cursorIndexOfEmploymentType)) {
              _tmpEmploymentType = null;
            } else {
              _tmpEmploymentType = _cursor.getString(_cursorIndexOfEmploymentType);
            }
            final double _tmpCurrentMonthlySalary;
            _tmpCurrentMonthlySalary = _cursor.getDouble(_cursorIndexOfCurrentMonthlySalary);
            final String _tmpJoinedAt;
            if (_cursor.isNull(_cursorIndexOfJoinedAt)) {
              _tmpJoinedAt = null;
            } else {
              _tmpJoinedAt = _cursor.getString(_cursorIndexOfJoinedAt);
            }
            final int _tmpLeaveAnnualLimit;
            _tmpLeaveAnnualLimit = _cursor.getInt(_cursorIndexOfLeaveAnnualLimit);
            final int _tmpLeaveCasualLimit;
            _tmpLeaveCasualLimit = _cursor.getInt(_cursorIndexOfLeaveCasualLimit);
            final int _tmpLeaveSickLimit;
            _tmpLeaveSickLimit = _cursor.getInt(_cursorIndexOfLeaveSickLimit);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new BrandAmbassadorEntity(_tmpId,_tmpName,_tmpMobile,_tmpCnic,_tmpStationId,_tmpStationName,_tmpAppPin,_tmpIsActive,_tmpEmploymentType,_tmpCurrentMonthlySalary,_tmpJoinedAt,_tmpLeaveAnnualLimit,_tmpLeaveCasualLimit,_tmpLeaveSickLimit,_tmpUpdatedAt);
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
  public Object getByPin(final String pin,
      final Continuation<? super BrandAmbassadorEntity> $completion) {
    final String _sql = "SELECT * FROM brand_ambassadors WHERE appPin = ? AND isActive = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pin);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BrandAmbassadorEntity>() {
      @Override
      @Nullable
      public BrandAmbassadorEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "mobile");
          final int _cursorIndexOfCnic = CursorUtil.getColumnIndexOrThrow(_cursor, "cnic");
          final int _cursorIndexOfStationId = CursorUtil.getColumnIndexOrThrow(_cursor, "stationId");
          final int _cursorIndexOfStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "stationName");
          final int _cursorIndexOfAppPin = CursorUtil.getColumnIndexOrThrow(_cursor, "appPin");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfEmploymentType = CursorUtil.getColumnIndexOrThrow(_cursor, "employmentType");
          final int _cursorIndexOfCurrentMonthlySalary = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMonthlySalary");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfLeaveAnnualLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "leaveAnnualLimit");
          final int _cursorIndexOfLeaveCasualLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "leaveCasualLimit");
          final int _cursorIndexOfLeaveSickLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "leaveSickLimit");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final BrandAmbassadorEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMobile;
            if (_cursor.isNull(_cursorIndexOfMobile)) {
              _tmpMobile = null;
            } else {
              _tmpMobile = _cursor.getString(_cursorIndexOfMobile);
            }
            final String _tmpCnic;
            if (_cursor.isNull(_cursorIndexOfCnic)) {
              _tmpCnic = null;
            } else {
              _tmpCnic = _cursor.getString(_cursorIndexOfCnic);
            }
            final String _tmpStationId;
            if (_cursor.isNull(_cursorIndexOfStationId)) {
              _tmpStationId = null;
            } else {
              _tmpStationId = _cursor.getString(_cursorIndexOfStationId);
            }
            final String _tmpStationName;
            if (_cursor.isNull(_cursorIndexOfStationName)) {
              _tmpStationName = null;
            } else {
              _tmpStationName = _cursor.getString(_cursorIndexOfStationName);
            }
            final String _tmpAppPin;
            _tmpAppPin = _cursor.getString(_cursorIndexOfAppPin);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpEmploymentType;
            if (_cursor.isNull(_cursorIndexOfEmploymentType)) {
              _tmpEmploymentType = null;
            } else {
              _tmpEmploymentType = _cursor.getString(_cursorIndexOfEmploymentType);
            }
            final double _tmpCurrentMonthlySalary;
            _tmpCurrentMonthlySalary = _cursor.getDouble(_cursorIndexOfCurrentMonthlySalary);
            final String _tmpJoinedAt;
            if (_cursor.isNull(_cursorIndexOfJoinedAt)) {
              _tmpJoinedAt = null;
            } else {
              _tmpJoinedAt = _cursor.getString(_cursorIndexOfJoinedAt);
            }
            final int _tmpLeaveAnnualLimit;
            _tmpLeaveAnnualLimit = _cursor.getInt(_cursorIndexOfLeaveAnnualLimit);
            final int _tmpLeaveCasualLimit;
            _tmpLeaveCasualLimit = _cursor.getInt(_cursorIndexOfLeaveCasualLimit);
            final int _tmpLeaveSickLimit;
            _tmpLeaveSickLimit = _cursor.getInt(_cursorIndexOfLeaveSickLimit);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new BrandAmbassadorEntity(_tmpId,_tmpName,_tmpMobile,_tmpCnic,_tmpStationId,_tmpStationName,_tmpAppPin,_tmpIsActive,_tmpEmploymentType,_tmpCurrentMonthlySalary,_tmpJoinedAt,_tmpLeaveAnnualLimit,_tmpLeaveCasualLimit,_tmpLeaveSickLimit,_tmpUpdatedAt);
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
