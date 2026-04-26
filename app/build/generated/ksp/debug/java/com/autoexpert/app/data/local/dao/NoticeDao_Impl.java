package com.autoexpert.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.autoexpert.app.data.local.entity.NoticeEntity;
import java.lang.Class;
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
public final class NoticeDao_Impl implements NoticeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NoticeEntity> __insertionAdapterOfNoticeEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkRead;

  private final SharedSQLiteStatement __preparedStmtOfMarkAllRead;

  public NoticeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNoticeEntity = new EntityInsertionAdapter<NoticeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `notices` (`id`,`message`,`targetBaIds`,`isActive`,`postedAt`,`isRead`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NoticeEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getMessage());
        if (entity.getTargetBaIds() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTargetBaIds());
        }
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getPostedAt());
        final int _tmp_1 = entity.isRead() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
      }
    };
    this.__preparedStmtOfMarkRead = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notices SET isRead = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAllRead = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notices SET isRead = 1";
        return _query;
      }
    };
  }

  @Override
  public Object upsertAll(final List<NoticeEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNoticeEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markRead(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkRead.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
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
          __preparedStmtOfMarkRead.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAllRead(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAllRead.acquire();
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
          __preparedStmtOfMarkAllRead.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<NoticeEntity>> getAll() {
    final String _sql = "SELECT * FROM notices WHERE isActive = 1 ORDER BY postedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notices"}, new Callable<List<NoticeEntity>>() {
      @Override
      @NonNull
      public List<NoticeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfTargetBaIds = CursorUtil.getColumnIndexOrThrow(_cursor, "targetBaIds");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfPostedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "postedAt");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final List<NoticeEntity> _result = new ArrayList<NoticeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoticeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMessage;
            _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            final String _tmpTargetBaIds;
            if (_cursor.isNull(_cursorIndexOfTargetBaIds)) {
              _tmpTargetBaIds = null;
            } else {
              _tmpTargetBaIds = _cursor.getString(_cursorIndexOfTargetBaIds);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpPostedAt;
            _tmpPostedAt = _cursor.getLong(_cursorIndexOfPostedAt);
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            _item = new NoticeEntity(_tmpId,_tmpMessage,_tmpTargetBaIds,_tmpIsActive,_tmpPostedAt,_tmpIsRead);
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
  public Flow<Integer> getUnreadCount() {
    final String _sql = "SELECT COUNT(*) FROM notices WHERE isActive = 1 AND isRead = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notices"}, new Callable<Integer>() {
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
