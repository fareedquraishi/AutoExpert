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
import com.autoexpert.app.data.local.entity.BaCommissionOverrideEntity;
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
public final class BaCommissionOverrideDao_Impl implements BaCommissionOverrideDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BaCommissionOverrideEntity> __insertionAdapterOfBaCommissionOverrideEntity;

  public BaCommissionOverrideDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBaCommissionOverrideEntity = new EntityInsertionAdapter<BaCommissionOverrideEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ba_commission_overrides` (`id`,`baId`,`packageId`,`effectiveFrom`,`effectiveTo`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BaCommissionOverrideEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getBaId());
        statement.bindString(3, entity.getPackageId());
        statement.bindString(4, entity.getEffectiveFrom());
        if (entity.getEffectiveTo() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getEffectiveTo());
        }
      }
    };
  }

  @Override
  public Object upsertAll(final List<BaCommissionOverrideEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBaCommissionOverrideEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActiveForBa(final String baId, final String today,
      final Continuation<? super BaCommissionOverrideEntity> $completion) {
    final String _sql = "SELECT * FROM ba_commission_overrides WHERE baId = ? AND (effectiveTo IS NULL OR effectiveTo >= ?) LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    _argIndex = 2;
    _statement.bindString(_argIndex, today);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BaCommissionOverrideEntity>() {
      @Override
      @Nullable
      public BaCommissionOverrideEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfPackageId = CursorUtil.getColumnIndexOrThrow(_cursor, "packageId");
          final int _cursorIndexOfEffectiveFrom = CursorUtil.getColumnIndexOrThrow(_cursor, "effectiveFrom");
          final int _cursorIndexOfEffectiveTo = CursorUtil.getColumnIndexOrThrow(_cursor, "effectiveTo");
          final BaCommissionOverrideEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpBaId;
            _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            final String _tmpPackageId;
            _tmpPackageId = _cursor.getString(_cursorIndexOfPackageId);
            final String _tmpEffectiveFrom;
            _tmpEffectiveFrom = _cursor.getString(_cursorIndexOfEffectiveFrom);
            final String _tmpEffectiveTo;
            if (_cursor.isNull(_cursorIndexOfEffectiveTo)) {
              _tmpEffectiveTo = null;
            } else {
              _tmpEffectiveTo = _cursor.getString(_cursorIndexOfEffectiveTo);
            }
            _result = new BaCommissionOverrideEntity(_tmpId,_tmpBaId,_tmpPackageId,_tmpEffectiveFrom,_tmpEffectiveTo);
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
  public Object getActiveOverride(final String baId, final String today,
      final Continuation<? super BaCommissionOverrideEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM ba_commission_overrides\n"
            + "        WHERE baId = ?\n"
            + "        AND effectiveFrom <= ?\n"
            + "        AND (effectiveTo IS NULL OR effectiveTo >= ?)\n"
            + "        ORDER BY effectiveFrom DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    _argIndex = 2;
    _statement.bindString(_argIndex, today);
    _argIndex = 3;
    _statement.bindString(_argIndex, today);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BaCommissionOverrideEntity>() {
      @Override
      @Nullable
      public BaCommissionOverrideEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfPackageId = CursorUtil.getColumnIndexOrThrow(_cursor, "packageId");
          final int _cursorIndexOfEffectiveFrom = CursorUtil.getColumnIndexOrThrow(_cursor, "effectiveFrom");
          final int _cursorIndexOfEffectiveTo = CursorUtil.getColumnIndexOrThrow(_cursor, "effectiveTo");
          final BaCommissionOverrideEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpBaId;
            _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            final String _tmpPackageId;
            _tmpPackageId = _cursor.getString(_cursorIndexOfPackageId);
            final String _tmpEffectiveFrom;
            _tmpEffectiveFrom = _cursor.getString(_cursorIndexOfEffectiveFrom);
            final String _tmpEffectiveTo;
            if (_cursor.isNull(_cursorIndexOfEffectiveTo)) {
              _tmpEffectiveTo = null;
            } else {
              _tmpEffectiveTo = _cursor.getString(_cursorIndexOfEffectiveTo);
            }
            _result = new BaCommissionOverrideEntity(_tmpId,_tmpBaId,_tmpPackageId,_tmpEffectiveFrom,_tmpEffectiveTo);
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
