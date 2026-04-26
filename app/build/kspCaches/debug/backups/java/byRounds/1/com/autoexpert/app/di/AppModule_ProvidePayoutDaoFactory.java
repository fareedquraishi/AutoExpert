package com.autoexpert.app.di;

import com.autoexpert.app.data.local.dao.PayoutDao;
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
public final class AppModule_ProvidePayoutDaoFactory implements Factory<PayoutDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvidePayoutDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PayoutDao get() {
    return providePayoutDao(dbProvider.get());
  }

  public static AppModule_ProvidePayoutDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvidePayoutDaoFactory(dbProvider);
  }

  public static PayoutDao providePayoutDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePayoutDao(db));
  }
}
