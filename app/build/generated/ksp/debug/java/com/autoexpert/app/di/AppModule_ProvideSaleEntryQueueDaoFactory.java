package com.autoexpert.app.di;

import com.autoexpert.app.data.local.dao.SaleEntryQueueDao;
import com.autoexpert.app.data.local.db.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideSaleEntryQueueDaoFactory implements Factory<SaleEntryQueueDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideSaleEntryQueueDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SaleEntryQueueDao get() {
    return provideSaleEntryQueueDao(dbProvider.get());
  }

  public static AppModule_ProvideSaleEntryQueueDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideSaleEntryQueueDaoFactory(dbProvider);
  }

  public static SaleEntryQueueDao provideSaleEntryQueueDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSaleEntryQueueDao(db));
  }
}
