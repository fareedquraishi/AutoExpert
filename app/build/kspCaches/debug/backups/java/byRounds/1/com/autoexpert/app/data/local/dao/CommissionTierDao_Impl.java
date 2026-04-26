package com.autoexpert.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.autoexpert.app.data.local.entity.CommissionTierEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CommissionTierDao_Impl implements CommissionTierDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CommissionTierEntity> __insertionAdapterOfCommissionTierEntity;

  public CommissionTierDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCommissionTierEntity = new EntityInsertionAdapter<CommissionTierEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `commission_tiers` (`id`,`packageId`,`minQty`,`maxQty`,`rate`,`sortOrder`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CommissionTierEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPackageId());
        statement.bindDouble(3, entity.getMinQty());
        if (entity.getMaxQty() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getMaxQty());
        }
        statement.bindDouble(5, entity.getRate());
        statement.bindLong(6, entity.getSortOrder());
      }
    };
  }

  @Override
  public Object upsertAll(final List<CommissionTierEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCommissionTierEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByPackage(final String packageId,
      final Continuation<? super List<CommissionTierEntity>> $completion) {
    final String _sql = "SELECT * FROM commission_tiers WHERE packageId = ? ORDER BY sortOrder";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, packageId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CommissionTierEntity>>() {
      @Override
      @NonNull
      public List<CommissionTierEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageId = CursorUtil.getColumnIndexOrThrow(_cursor, "packageId");
          final int _cursorIndexOfMinQty = CursorUtil.getColumnIndexOrThrow(_cursor, "minQty");
          final int _cursorIndexOfMaxQty = CursorUtil.getColumnIndexOrThrow(_cursor, "maxQty");
          final int _cursorIndexOfRate = CursorUtil.getColumnIndexOrThrow(_cursor, "rate");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<CommissionTierEntity> _result = new ArrayList<CommissionTierEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CommissionTierEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPackageId;
            _tmpPackageId = _cursor.getString(_cursorIndexOfPackageId);
            final double _tmpMinQty;
            _tmpMinQty = _cursor.getDouble(_cursorIndexOfMinQty);
            final Double _tmpMaxQty;
            if (_cursor.isNull(_cursorIndexOfMaxQty)) {
              _tmpMaxQty = null;
            } else {
              _tmpMaxQty = _cursor.getDouble(_cursorIndexOfMaxQty);
            }
            final double _tmpRate;
            _tmpRate = _cursor.getDouble(_cursorIndexOfRate);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new CommissionTierEntity(_tmpId,_tmpPackageId,_tmpMinQty,_tmpMaxQty,_tmpRate,_tmpSortOrder);
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
  public Object getAll(final Continuation<? super List<CommissionTierEntity>> $completion) {
    final String _sql = "SELECT * FROM commission_tiers ORDER BY sortOrder";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CommissionTierEntity>>() {
      @Override
      @NonNull
      public List<CommissionTierEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageId = CursorUtil.getColumnIndexOrThrow(_cursor, "packageId");
          final int _cursorIndexOfMinQty = CursorUtil.getColumnIndexOrThrow(_cursor, "minQty");
          final int _cursorIndexOfMaxQty = CursorUtil.getColumnIndexOrThrow(_cursor, "maxQty");
          final int _cursorIndexOfRate = CursorUtil.getColumnIndexOrThrow(_cursor, "rate");
          final int _cursorIndexOfSortOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sortOrder");
          final List<CommissionTierEntity> _result = new ArrayList<CommissionTierEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CommissionTierEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPackageId;
            _tmpPackageId = _cursor.getString(_cursorIndexOfPackageId);
            final double _tmpMinQty;
            _tmpMinQty = _cursor.getDouble(_cursorIndexOfMinQty);
            final Double _tmpMaxQty;
            if (_cursor.isNull(_cursorIndexOfMaxQty)) {
              _tmpMaxQty = null;
            } else {
              _tmpMaxQty = _cursor.getDouble(_cursorIndexOfMaxQty);
            }
            final double _tmpRate;
            _tmpRate = _cursor.getDouble(_cursorIndexOfRate);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new CommissionTierEntity(_tmpId,_tmpPackageId,_tmpMinQty,_tmpMaxQty,_tmpRate,_tmpSortOrder);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
