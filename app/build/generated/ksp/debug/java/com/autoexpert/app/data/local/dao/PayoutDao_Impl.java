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
import com.autoexpert.app.data.local.entity.PayoutEntity;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PayoutDao_Impl implements PayoutDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PayoutEntity> __insertionAdapterOfPayoutEntity;

  public PayoutDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPayoutEntity = new EntityInsertionAdapter<PayoutEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `payouts` (`id`,`baId`,`payoutDate`,`amount`,`note`,`createdAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PayoutEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getBaId());
        statement.bindString(3, entity.getPayoutDate());
        statement.bindDouble(4, entity.getAmount());
        if (entity.getNote() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNote());
        }
        statement.bindLong(6, entity.getCreatedAt());
      }
    };
  }

  @Override
  public Object upsertAll(final List<PayoutEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPayoutEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PayoutEntity>> getByBa(final String baId) {
    final String _sql = "SELECT * FROM payouts WHERE baId = ? ORDER BY payoutDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payouts"}, new Callable<List<PayoutEntity>>() {
      @Override
      @NonNull
      public List<PayoutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfPayoutDate = CursorUtil.getColumnIndexOrThrow(_cursor, "payoutDate");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<PayoutEntity> _result = new ArrayList<PayoutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PayoutEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpBaId;
            _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            final String _tmpPayoutDate;
            _tmpPayoutDate = _cursor.getString(_cursorIndexOfPayoutDate);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new PayoutEntity(_tmpId,_tmpBaId,_tmpPayoutDate,_tmpAmount,_tmpNote,_tmpCreatedAt);
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
  public Object getTotalPaid(final String baId, final Continuation<? super Double> $completion) {
    final String _sql = "SELECT SUM(amount) FROM payouts WHERE baId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
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
