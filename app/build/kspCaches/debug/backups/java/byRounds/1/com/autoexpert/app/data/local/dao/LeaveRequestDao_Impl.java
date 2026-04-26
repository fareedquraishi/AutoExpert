package com.autoexpert.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.autoexpert.app.data.local.entity.LeaveRequestEntity;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LeaveRequestDao_Impl implements LeaveRequestDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LeaveRequestEntity> __insertionAdapterOfLeaveRequestEntity;

  public LeaveRequestDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLeaveRequestEntity = new EntityInsertionAdapter<LeaveRequestEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `leave_requests` (`id`,`baId`,`leaveType`,`fromDate`,`toDate`,`totalDays`,`reason`,`status`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LeaveRequestEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getBaId());
        statement.bindString(3, entity.getLeaveType());
        statement.bindString(4, entity.getFromDate());
        statement.bindString(5, entity.getToDate());
        statement.bindLong(6, entity.getTotalDays());
        if (entity.getReason() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getReason());
        }
        statement.bindString(8, entity.getStatus());
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
  }

  @Override
  public Object upsertAll(final List<LeaveRequestEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLeaveRequestEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insert(final LeaveRequestEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLeaveRequestEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<LeaveRequestEntity>> getByBa(final String baId) {
    final String _sql = "SELECT * FROM leave_requests WHERE baId = ? ORDER BY fromDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"leave_requests"}, new Callable<List<LeaveRequestEntity>>() {
      @Override
      @NonNull
      public List<LeaveRequestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfLeaveType = CursorUtil.getColumnIndexOrThrow(_cursor, "leaveType");
          final int _cursorIndexOfFromDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fromDate");
          final int _cursorIndexOfToDate = CursorUtil.getColumnIndexOrThrow(_cursor, "toDate");
          final int _cursorIndexOfTotalDays = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDays");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<LeaveRequestEntity> _result = new ArrayList<LeaveRequestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LeaveRequestEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpBaId;
            _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            final String _tmpLeaveType;
            _tmpLeaveType = _cursor.getString(_cursorIndexOfLeaveType);
            final String _tmpFromDate;
            _tmpFromDate = _cursor.getString(_cursorIndexOfFromDate);
            final String _tmpToDate;
            _tmpToDate = _cursor.getString(_cursorIndexOfToDate);
            final int _tmpTotalDays;
            _tmpTotalDays = _cursor.getInt(_cursorIndexOfTotalDays);
            final String _tmpReason;
            if (_cursor.isNull(_cursorIndexOfReason)) {
              _tmpReason = null;
            } else {
              _tmpReason = _cursor.getString(_cursorIndexOfReason);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new LeaveRequestEntity(_tmpId,_tmpBaId,_tmpLeaveType,_tmpFromDate,_tmpToDate,_tmpTotalDays,_tmpReason,_tmpStatus,_tmpCreatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
