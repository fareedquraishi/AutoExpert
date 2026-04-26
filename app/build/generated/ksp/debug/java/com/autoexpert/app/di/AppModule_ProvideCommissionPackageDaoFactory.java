package com.autoexpert.app.di;

import com.autoexpert.app.data.local.dao.CommissionPackageDao;
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
public final class AppModule_ProvideCommissionPackageDaoFactory implements Factory<CommissionPackageDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideCommissionPackageDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CommissionPackageDao get() {
    return provideCommissionPackageDao(dbProvider.get());
  }

  public static AppModule_ProvideCommissionPackageDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideCommissionPackageDaoFactory(dbProvider);
  }

  public static CommissionPackageDao provideCommissionPackageDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCommissionPackageDao(db));
  }
}
