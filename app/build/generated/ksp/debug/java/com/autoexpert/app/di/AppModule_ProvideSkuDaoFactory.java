package com.autoexpert.app.di;

import com.autoexpert.app.data.local.dao.SkuDao;
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
public final class AppModule_ProvideSkuDaoFactory implements Factory<SkuDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideSkuDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SkuDao get() {
    return provideSkuDao(dbProvider.get());
  }

  public static AppModule_ProvideSkuDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideSkuDaoFactory(dbProvider);
  }

  public static SkuDao provideSkuDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSkuDao(db));
  }
}
