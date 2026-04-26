package com.autoexpert.app.di;

import com.autoexpert.app.data.local.dao.BaCommissionOverrideDao;
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
public final class AppModule_ProvideBaCommissionOverrideDaoFactory implements Factory<BaCommissionOverrideDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideBaCommissionOverrideDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BaCommissionOverrideDao get() {
    return provideBaCommissionOverrideDao(dbProvider.get());
  }

  public static AppModule_ProvideBaCommissionOverrideDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideBaCommissionOverrideDaoFactory(dbProvider);
  }

  public static BaCommissionOverrideDao provideBaCommissionOverrideDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBaCommissionOverrideDao(db));
  }
}
