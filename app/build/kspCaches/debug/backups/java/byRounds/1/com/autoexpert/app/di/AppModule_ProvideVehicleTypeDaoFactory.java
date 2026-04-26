package com.autoexpert.app.di;

import com.autoexpert.app.data.local.dao.VehicleTypeDao;
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
public final class AppModule_ProvideVehicleTypeDaoFactory implements Factory<VehicleTypeDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideVehicleTypeDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public VehicleTypeDao get() {
    return provideVehicleTypeDao(dbProvider.get());
  }

  public static AppModule_ProvideVehicleTypeDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideVehicleTypeDaoFactory(dbProvider);
  }

  public static VehicleTypeDao provideVehicleTypeDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideVehicleTypeDao(db));
  }
}
