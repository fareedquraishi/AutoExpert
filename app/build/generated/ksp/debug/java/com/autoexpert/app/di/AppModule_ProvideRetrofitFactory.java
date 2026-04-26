package com.autoexpert.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideRetrofitFactory implements Factory<Retrofit> {
  private final Provider<OkHttpClient> okHttpProvider;

  public AppModule_ProvideRetrofitFactory(Provider<OkHttpClient> okHttpProvider) {
    this.okHttpProvider = okHttpProvider;
  }

  @Override
  public Retrofit get() {
    return provideRetrofit(okHttpProvider.get());
  }

  public static AppModule_ProvideRetrofitFactory create(Provider<OkHttpClient> okHttpProvider) {
    return new AppModule_ProvideRetrofitFactory(okHttpProvider);
  }

  public static Retrofit provideRetrofit(OkHttpClient okHttp) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRetrofit(okHttp));
  }
}
