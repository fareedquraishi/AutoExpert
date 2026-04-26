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
import com.autoexpert.app.data.local.entity.TargetEntity;
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
public final class TargetDao_Impl implements TargetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TargetEntity> __insertionAdapterOfTargetEntity;

  public TargetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTargetEntity = new EntityInsertionAdapter<TargetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `targets` (`id`,`baId`,`stationId`,`period`,`targetValue`,`targetBasis`,`effectiveFrom`,`effectiveTo`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TargetEntity entity) {
        statement.bindString(1, entity.getId());
        if (entity.getBaId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getBaId());
        }
        if (entity.getStationId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getStationId());
        }
        statement.bindString(4, entity.getPeriod());
        statement.bindDouble(5, entity.getTargetValue());
        statement.bindString(6, entity.getTargetBasis());
        statement.bindString(7, entity.getEffectiveFrom());
        if (entity.getEffectiveTo() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getEffectiveTo());
        }
      }
    };
  }

  @Override
  public Object upsertAll(final List<TargetEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTargetEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActiveTarget(final String baId, final String stationId, final String today,
      final Continuation<? super TargetEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM targets\n"
            + "        WHERE (baId = ? OR stationId = ? OR (baId IS NULL AND stationId IS NULL))\n"
            + "        AND period = 'daily'\n"
            + "        AND effectiveFrom <= ?\n"
            + "        AND (effectiveTo IS NULL OR effectiveTo >= ?)\n"
            + "        ORDER BY CASE WHEN baId IS NOT NULL THEN 0 WHEN stationId IS NOT NULL THEN 1 ELSE 2 END\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    _argIndex = 2;
    _statement.bindString(_argIndex, stationId);
    _argIndex = 3;
    _statement.bindString(_argIndex, today);
    _argIndex = 4;
    _statement.bindString(_argIndex, today);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TargetEntity>() {
      @Override
      @Nullable
      public TargetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfStationId = CursorUtil.getColumnIndexOrThrow(_cursor, "stationId");
          final int _cursorIndexOfPeriod = CursorUtil.getColumnIndexOrThrow(_cursor, "period");
          final int _cursorIndexOfTargetValue = CursorUtil.getColumnIndexOrThrow(_cursor, "targetValue");
          final int _cursorIndexOfTargetBasis = CursorUtil.getColumnIndexOrThrow(_cursor, "targetBasis");
          final int _cursorIndexOfEffectiveFrom = CursorUtil.getColumnIndexOrThrow(_cursor, "effectiveFrom");
          final int _cursorIndexOfEffectiveTo = CursorUtil.getColumnIndexOrThrow(_cursor, "effectiveTo");
          final TargetEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpBaId;
            if (_cursor.isNull(_cursorIndexOfBaId)) {
              _tmpBaId = null;
            } else {
              _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            }
            final String _tmpStationId;
            if (_cursor.isNull(_cursorIndexOfStationId)) {
              _tmpStationId = null;
            } else {
              _tmpStationId = _cursor.getString(_cursorIndexOfStationId);
            }
            final String _tmpPeriod;
            _tmpPeriod = _cursor.getString(_cursorIndexOfPeriod);
            final double _tmpTargetValue;
            _tmpTargetValue = _cursor.getDouble(_cursorIndexOfTargetValue);
            final String _tmpTargetBasis;
            _tmpTargetBasis = _cursor.getString(_cursorIndexOfTargetBasis);
            final String _tmpEffectiveFrom;
            _tmpEffectiveFrom = _cursor.getString(_cursorIndexOfEffectiveFrom);
            final String _tmpEffectiveTo;
            if (_cursor.isNull(_cursorIndexOfEffectiveTo)) {
              _tmpEffectiveTo = null;
            } else {
              _tmpEffectiveTo = _cursor.getString(_cursorIndexOfEffectiveTo);
            }
            _result = new TargetEntity(_tmpId,_tmpBaId,_tmpStationId,_tmpPeriod,_tmpTargetValue,_tmpTargetBasis,_tmpEffectiveFrom,_tmpEffectiveTo);
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
