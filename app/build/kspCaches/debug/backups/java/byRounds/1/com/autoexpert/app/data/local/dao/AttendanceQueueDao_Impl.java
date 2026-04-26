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
import com.autoexpert.app.data.local.entity.AttendanceQueueEntity;
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
public final class AttendanceQueueDao_Impl implements AttendanceQueueDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AttendanceQueueEntity> __insertionAdapterOfAttendanceQueueEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  public AttendanceQueueDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAttendanceQueueEntity = new EntityInsertionAdapter<AttendanceQueueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `attendance_queue` (`localId`,`remoteId`,`baId`,`attendanceDate`,`isPresent`,`attendanceStatus`,`method`,`checkInTime`,`geoLatitude`,`geoLongitude`,`note`,`syncStatus`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AttendanceQueueEntity entity) {
        statement.bindString(1, entity.getLocalId());
        if (entity.getRemoteId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getRemoteId());
        }
        statement.bindString(3, entity.getBaId());
        statement.bindString(4, entity.getAttendanceDate());
        final int _tmp = entity.isPresent() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getAttendanceStatus() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getAttendanceStatus());
        }
        statement.bindString(7, entity.getMethod());
        if (entity.getCheckInTime() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCheckInTime());
        }
        if (entity.getGeoLatitude() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getGeoLatitude());
        }
        if (entity.getGeoLongitude() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getGeoLongitude());
        }
        if (entity.getNote() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getNote());
        }
        statement.bindString(12, entity.getSyncStatus());
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE attendance_queue SET syncStatus = ?, remoteId = ? WHERE localId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final AttendanceQueueEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAttendanceQueueEntity.insert(item);
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
  public Object getPending(final Continuation<? super List<AttendanceQueueEntity>> $completion) {
    final String _sql = "SELECT * FROM attendance_queue WHERE syncStatus = 'pending'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AttendanceQueueEntity>>() {
      @Override
      @NonNull
      public List<AttendanceQueueEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfAttendanceDate = CursorUtil.getColumnIndexOrThrow(_cursor, "attendanceDate");
          final int _cursorIndexOfIsPresent = CursorUtil.getColumnIndexOrThrow(_cursor, "isPresent");
          final int _cursorIndexOfAttendanceStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "attendanceStatus");
          final int _cursorIndexOfMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "method");
          final int _cursorIndexOfCheckInTime = CursorUtil.getColumnIndexOrThrow(_cursor, "checkInTime");
          final int _cursorIndexOfGeoLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "geoLatitude");
          final int _cursorIndexOfGeoLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "geoLongitude");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<AttendanceQueueEntity> _result = new ArrayList<AttendanceQueueEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AttendanceQueueEntity _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final String _tmpBaId;
            _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            final String _tmpAttendanceDate;
            _tmpAttendanceDate = _cursor.getString(_cursorIndexOfAttendanceDate);
            final boolean _tmpIsPresent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPresent);
            _tmpIsPresent = _tmp != 0;
            final String _tmpAttendanceStatus;
            if (_cursor.isNull(_cursorIndexOfAttendanceStatus)) {
              _tmpAttendanceStatus = null;
            } else {
              _tmpAttendanceStatus = _cursor.getString(_cursorIndexOfAttendanceStatus);
            }
            final String _tmpMethod;
            _tmpMethod = _cursor.getString(_cursorIndexOfMethod);
            final String _tmpCheckInTime;
            if (_cursor.isNull(_cursorIndexOfCheckInTime)) {
              _tmpCheckInTime = null;
            } else {
              _tmpCheckInTime = _cursor.getString(_cursorIndexOfCheckInTime);
            }
            final Double _tmpGeoLatitude;
            if (_cursor.isNull(_cursorIndexOfGeoLatitude)) {
              _tmpGeoLatitude = null;
            } else {
              _tmpGeoLatitude = _cursor.getDouble(_cursorIndexOfGeoLatitude);
            }
            final Double _tmpGeoLongitude;
            if (_cursor.isNull(_cursorIndexOfGeoLongitude)) {
              _tmpGeoLongitude = null;
            } else {
              _tmpGeoLongitude = _cursor.getDouble(_cursorIndexOfGeoLongitude);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _item = new AttendanceQueueEntity(_tmpLocalId,_tmpRemoteId,_tmpBaId,_tmpAttendanceDate,_tmpIsPresent,_tmpAttendanceStatus,_tmpMethod,_tmpCheckInTime,_tmpGeoLatitude,_tmpGeoLongitude,_tmpNote,_tmpSyncStatus);
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
  public Flow<List<AttendanceQueueEntity>> getByBa(final String baId) {
    final String _sql = "SELECT * FROM attendance_queue WHERE baId = ? ORDER BY attendanceDate DESC LIMIT 30";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"attendance_queue"}, new Callable<List<AttendanceQueueEntity>>() {
      @Override
      @NonNull
      public List<AttendanceQueueEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfAttendanceDate = CursorUtil.getColumnIndexOrThrow(_cursor, "attendanceDate");
          final int _cursorIndexOfIsPresent = CursorUtil.getColumnIndexOrThrow(_cursor, "isPresent");
          final int _cursorIndexOfAttendanceStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "attendanceStatus");
          final int _cursorIndexOfMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "method");
          final int _cursorIndexOfCheckInTime = CursorUtil.getColumnIndexOrThrow(_cursor, "checkInTime");
          final int _cursorIndexOfGeoLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "geoLatitude");
          final int _cursorIndexOfGeoLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "geoLongitude");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<AttendanceQueueEntity> _result = new ArrayList<AttendanceQueueEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AttendanceQueueEntity _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final String _tmpBaId;
            _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            final String _tmpAttendanceDate;
            _tmpAttendanceDate = _cursor.getString(_cursorIndexOfAttendanceDate);
            final boolean _tmpIsPresent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPresent);
            _tmpIsPresent = _tmp != 0;
            final String _tmpAttendanceStatus;
            if (_cursor.isNull(_cursorIndexOfAttendanceStatus)) {
              _tmpAttendanceStatus = null;
            } else {
              _tmpAttendanceStatus = _cursor.getString(_cursorIndexOfAttendanceStatus);
            }
            final String _tmpMethod;
            _tmpMethod = _cursor.getString(_cursorIndexOfMethod);
            final String _tmpCheckInTime;
            if (_cursor.isNull(_cursorIndexOfCheckInTime)) {
              _tmpCheckInTime = null;
            } else {
              _tmpCheckInTime = _cursor.getString(_cursorIndexOfCheckInTime);
            }
            final Double _tmpGeoLatitude;
            if (_cursor.isNull(_cursorIndexOfGeoLatitude)) {
              _tmpGeoLatitude = null;
            } else {
              _tmpGeoLatitude = _cursor.getDouble(_cursorIndexOfGeoLatitude);
            }
            final Double _tmpGeoLongitude;
            if (_cursor.isNull(_cursorIndexOfGeoLongitude)) {
              _tmpGeoLongitude = null;
            } else {
              _tmpGeoLongitude = _cursor.getDouble(_cursorIndexOfGeoLongitude);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _item = new AttendanceQueueEntity(_tmpLocalId,_tmpRemoteId,_tmpBaId,_tmpAttendanceDate,_tmpIsPresent,_tmpAttendanceStatus,_tmpMethod,_tmpCheckInTime,_tmpGeoLatitude,_tmpGeoLongitude,_tmpNote,_tmpSyncStatus);
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
  public Object getByBaAndDate(final String baId, final String date,
      final Continuation<? super AttendanceQueueEntity> $completion) {
    final String _sql = "SELECT * FROM attendance_queue WHERE baId = ? AND attendanceDate = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    _argIndex = 2;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AttendanceQueueEntity>() {
      @Override
      @Nullable
      public AttendanceQueueEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final int _cursorIndexOfBaId = CursorUtil.getColumnIndexOrThrow(_cursor, "baId");
          final int _cursorIndexOfAttendanceDate = CursorUtil.getColumnIndexOrThrow(_cursor, "attendanceDate");
          final int _cursorIndexOfIsPresent = CursorUtil.getColumnIndexOrThrow(_cursor, "isPresent");
          final int _cursorIndexOfAttendanceStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "attendanceStatus");
          final int _cursorIndexOfMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "method");
          final int _cursorIndexOfCheckInTime = CursorUtil.getColumnIndexOrThrow(_cursor, "checkInTime");
          final int _cursorIndexOfGeoLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "geoLatitude");
          final int _cursorIndexOfGeoLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "geoLongitude");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final AttendanceQueueEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            final String _tmpBaId;
            _tmpBaId = _cursor.getString(_cursorIndexOfBaId);
            final String _tmpAttendanceDate;
            _tmpAttendanceDate = _cursor.getString(_cursorIndexOfAttendanceDate);
            final boolean _tmpIsPresent;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPresent);
            _tmpIsPresent = _tmp != 0;
            final String _tmpAttendanceStatus;
            if (_cursor.isNull(_cursorIndexOfAttendanceStatus)) {
              _tmpAttendanceStatus = null;
            } else {
              _tmpAttendanceStatus = _cursor.getString(_cursorIndexOfAttendanceStatus);
            }
            final String _tmpMethod;
            _tmpMethod = _cursor.getString(_cursorIndexOfMethod);
            final String _tmpCheckInTime;
            if (_cursor.isNull(_cursorIndexOfCheckInTime)) {
              _tmpCheckInTime = null;
            } else {
              _tmpCheckInTime = _cursor.getString(_cursorIndexOfCheckInTime);
            }
            final Double _tmpGeoLatitude;
            if (_cursor.isNull(_cursorIndexOfGeoLatitude)) {
              _tmpGeoLatitude = null;
            } else {
              _tmpGeoLatitude = _cursor.getDouble(_cursorIndexOfGeoLatitude);
            }
            final Double _tmpGeoLongitude;
            if (_cursor.isNull(_cursorIndexOfGeoLongitude)) {
              _tmpGeoLongitude = null;
            } else {
              _tmpGeoLongitude = _cursor.getDouble(_cursorIndexOfGeoLongitude);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _result = new AttendanceQueueEntity(_tmpLocalId,_tmpRemoteId,_tmpBaId,_tmpAttendanceDate,_tmpIsPresent,_tmpAttendanceStatus,_tmpMethod,_tmpCheckInTime,_tmpGeoLatitude,_tmpGeoLongitude,_tmpNote,_tmpSyncStatus);
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
  public Object countPresentDays(final String baId, final String monthPrefix,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM attendance_queue WHERE baId = ? AND isPresent = 1 AND attendanceDate LIKE ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, baId);
    _argIndex = 2;
    _statement.bindString(_argIndex, monthPrefix);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
