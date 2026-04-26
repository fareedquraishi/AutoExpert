package com.autoexpert.app.di;

import com.autoexpert.app.data.local.dao.TargetDao;
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
public final class AppModule_ProvideTargetDaoFactory implements Factory<TargetDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideTargetDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TargetDao get() {
    return provideTargetDao(dbProvider.get());
  }

  public static AppModule_ProvideTargetDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideTargetDaoFactory(dbProvider);
  }

  public static TargetDao provideTargetDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTargetDao(db));
  }
}
