package com.autoexpert.app.di;

import com.autoexpert.app.data.local.dao.BrandAmbassadorDao;
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
public final class AppModule_ProvideBaDaoFactory implements Factory<BrandAmbassadorDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideBaDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BrandAmbassadorDao get() {
    return provideBaDao(dbProvider.get());
  }

  public static AppModule_ProvideBaDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideBaDaoFactory(dbProvider);
  }

  public static BrandAmbassadorDao provideBaDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBaDao(db));
  }
}
