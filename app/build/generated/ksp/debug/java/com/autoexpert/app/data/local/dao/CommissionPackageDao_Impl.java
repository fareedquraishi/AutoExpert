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
import com.autoexpert.app.data.local.entity.CommissionPackageEntity;
import java.lang.Class;
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
public final class CommissionPackageDao_Impl implements CommissionPackageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CommissionPackageEntity> __insertionAdapterOfCommissionPackageEntity;

  public CommissionPackageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCommissionPackageEntity = new EntityInsertionAdapter<CommissionPackageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `commission_packages` (`id`,`name`,`basis`,`minThresholdLitres`,`flatRate`,`isGlobal`,`isActive`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CommissionPackageEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getBasis());
        statement.bindDouble(4, entity.getMinThresholdLitres());
        statement.bindDouble(5, entity.getFlatRate());
        final int _tmp = entity.isGlobal() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isActive() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
      }
    };
  }

  @Override
  public Object upsertAll(final List<CommissionPackageEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCommissionPackageEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getGlobalActive(final Continuation<? super CommissionPackageEntity> $completion) {
    final String _sql = "SELECT * FROM commission_packages WHERE isGlobal = 1 AND isActive = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CommissionPackageEntity>() {
      @Override
      @Nullable
      public CommissionPackageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBasis = CursorUtil.getColumnIndexOrThrow(_cursor, "basis");
          final int _cursorIndexOfMinThresholdLitres = CursorUtil.getColumnIndexOrThrow(_cursor, "minThresholdLitres");
          final int _cursorIndexOfFlatRate = CursorUtil.getColumnIndexOrThrow(_cursor, "flatRate");
          final int _cursorIndexOfIsGlobal = CursorUtil.getColumnIndexOrThrow(_cursor, "isGlobal");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final CommissionPackageEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBasis;
            _tmpBasis = _cursor.getString(_cursorIndexOfBasis);
            final double _tmpMinThresholdLitres;
            _tmpMinThresholdLitres = _cursor.getDouble(_cursorIndexOfMinThresholdLitres);
            final double _tmpFlatRate;
            _tmpFlatRate = _cursor.getDouble(_cursorIndexOfFlatRate);
            final boolean _tmpIsGlobal;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsGlobal);
            _tmpIsGlobal = _tmp != 0;
            final boolean _tmpIsActive;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_1 != 0;
            _result = new CommissionPackageEntity(_tmpId,_tmpName,_tmpBasis,_tmpMinThresholdLitres,_tmpFlatRate,_tmpIsGlobal,_tmpIsActive);
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
  public Object getById(final String id,
      final Continuation<? super CommissionPackageEntity> $completion) {
    final String _sql = "SELECT * FROM commission_packages WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CommissionPackageEntity>() {
      @Override
      @Nullable
      public CommissionPackageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBasis = CursorUtil.getColumnIndexOrThrow(_cursor, "basis");
          final int _cursorIndexOfMinThresholdLitres = CursorUtil.getColumnIndexOrThrow(_cursor, "minThresholdLitres");
          final int _cursorIndexOfFlatRate = CursorUtil.getColumnIndexOrThrow(_cursor, "flatRate");
          final int _cursorIndexOfIsGlobal = CursorUtil.getColumnIndexOrThrow(_cursor, "isGlobal");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final CommissionPackageEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBasis;
            _tmpBasis = _cursor.getString(_cursorIndexOfBasis);
            final double _tmpMinThresholdLitres;
            _tmpMinThresholdLitres = _cursor.getDouble(_cursorIndexOfMinThresholdLitres);
            final double _tmpFlatRate;
            _tmpFlatRate = _cursor.getDouble(_cursorIndexOfFlatRate);
            final boolean _tmpIsGlobal;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsGlobal);
            _tmpIsGlobal = _tmp != 0;
            final boolean _tmpIsActive;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_1 != 0;
            _result = new CommissionPackageEntity(_tmpId,_tmpName,_tmpBasis,_tmpMinThresholdLitres,_tmpFlatRate,_tmpIsGlobal,_tmpIsActive);
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
  public Object getAllActive(
      final Continuation<? super List<CommissionPackageEntity>> $completion) {
    final String _sql = "SELECT * FROM commission_packages WHERE isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CommissionPackageEntity>>() {
      @Override
      @NonNull
      public List<CommissionPackageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfBasis = CursorUtil.getColumnIndexOrThrow(_cursor, "basis");
          final int _cursorIndexOfMinThresholdLitres = CursorUtil.getColumnIndexOrThrow(_cursor, "minThresholdLitres");
          final int _cursorIndexOfFlatRate = CursorUtil.getColumnIndexOrThrow(_cursor, "flatRate");
          final int _cursorIndexOfIsGlobal = CursorUtil.getColumnIndexOrThrow(_cursor, "isGlobal");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<CommissionPackageEntity> _result = new ArrayList<CommissionPackageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CommissionPackageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpBasis;
            _tmpBasis = _cursor.getString(_cursorIndexOfBasis);
            final double _tmpMinThresholdLitres;
            _tmpMinThresholdLitres = _cursor.getDouble(_cursorIndexOfMinThresholdLitres);
            final double _tmpFlatRate;
            _tmpFlatRate = _cursor.getDouble(_cursorIndexOfFlatRate);
            final boolean _tmpIsGlobal;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsGlobal);
            _tmpIsGlobal = _tmp != 0;
            final boolean _tmpIsActive;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_1 != 0;
            _item = new CommissionPackageEntity(_tmpId,_tmpName,_tmpBasis,_tmpMinThresholdLitres,_tmpFlatRate,_tmpIsGlobal,_tmpIsActive);
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
