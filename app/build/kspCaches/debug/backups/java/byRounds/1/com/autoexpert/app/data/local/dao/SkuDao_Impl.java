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
import com.autoexpert.app.data.local.entity.SkuEntity;
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
public final class SkuDao_Impl implements SkuDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SkuEntity> __insertionAdapterOfSkuEntity;

  public SkuDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSkuEntity = new EntityInsertionAdapter<SkuEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `skus` (`id`,`name`,`productType`,`volumeMl`,`purchasePrice`,`marginPercent`,`sellingPrice`,`isActive`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SkuEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getProductType() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getProductType());
        }
        statement.bindDouble(4, entity.getVolumeMl());
        statement.bindDouble(5, entity.getPurchasePrice());
        statement.bindDouble(6, entity.getMarginPercent());
        statement.bindDouble(7, entity.getSellingPrice());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
  }

  @Override
  public Object upsertAll(final List<SkuEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSkuEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SkuEntity>> getAllActive() {
    final String _sql = "SELECT * FROM skus WHERE isActive = 1 ORDER BY name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"skus"}, new Callable<List<SkuEntity>>() {
      @Override
      @NonNull
      public List<SkuEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfProductType = CursorUtil.getColumnIndexOrThrow(_cursor, "productType");
          final int _cursorIndexOfVolumeMl = CursorUtil.getColumnIndexOrThrow(_cursor, "volumeMl");
          final int _cursorIndexOfPurchasePrice = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasePrice");
          final int _cursorIndexOfMarginPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "marginPercent");
          final int _cursorIndexOfSellingPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "sellingPrice");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<SkuEntity> _result = new ArrayList<SkuEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SkuEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpProductType;
            if (_cursor.isNull(_cursorIndexOfProductType)) {
              _tmpProductType = null;
            } else {
              _tmpProductType = _cursor.getString(_cursorIndexOfProductType);
            }
            final double _tmpVolumeMl;
            _tmpVolumeMl = _cursor.getDouble(_cursorIndexOfVolumeMl);
            final double _tmpPurchasePrice;
            _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
            final double _tmpMarginPercent;
            _tmpMarginPercent = _cursor.getDouble(_cursorIndexOfMarginPercent);
            final double _tmpSellingPrice;
            _tmpSellingPrice = _cursor.getDouble(_cursorIndexOfSellingPrice);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new SkuEntity(_tmpId,_tmpName,_tmpProductType,_tmpVolumeMl,_tmpPurchasePrice,_tmpMarginPercent,_tmpSellingPrice,_tmpIsActive);
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
  public Object getAllActiveOnce(final Continuation<? super List<SkuEntity>> $completion) {
    final String _sql = "SELECT * FROM skus WHERE isActive = 1 ORDER BY name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SkuEntity>>() {
      @Override
      @NonNull
      public List<SkuEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfProductType = CursorUtil.getColumnIndexOrThrow(_cursor, "productType");
          final int _cursorIndexOfVolumeMl = CursorUtil.getColumnIndexOrThrow(_cursor, "volumeMl");
          final int _cursorIndexOfPurchasePrice = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasePrice");
          final int _cursorIndexOfMarginPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "marginPercent");
          final int _cursorIndexOfSellingPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "sellingPrice");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<SkuEntity> _result = new ArrayList<SkuEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SkuEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpProductType;
            if (_cursor.isNull(_cursorIndexOfProductType)) {
              _tmpProductType = null;
            } else {
              _tmpProductType = _cursor.getString(_cursorIndexOfProductType);
            }
            final double _tmpVolumeMl;
            _tmpVolumeMl = _cursor.getDouble(_cursorIndexOfVolumeMl);
            final double _tmpPurchasePrice;
            _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
            final double _tmpMarginPercent;
            _tmpMarginPercent = _cursor.getDouble(_cursorIndexOfMarginPercent);
            final double _tmpSellingPrice;
            _tmpSellingPrice = _cursor.getDouble(_cursorIndexOfSellingPrice);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new SkuEntity(_tmpId,_tmpName,_tmpProductType,_tmpVolumeMl,_tmpPurchasePrice,_tmpMarginPercent,_tmpSellingPrice,_tmpIsActive);
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
