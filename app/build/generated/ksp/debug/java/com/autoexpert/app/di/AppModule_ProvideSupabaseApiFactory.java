package com.autoexpert.app.di;

import com.autoexpert.app.data.remote.api.SupabaseApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
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
public final class AppModule_ProvideSupabaseApiFactory implements Factory<SupabaseApi> {
  private final Provider<Retrofit> retrofitProvider;

  public AppModule_ProvideSupabaseApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public SupabaseApi get() {
    return provideSupabaseApi(retrofitProvider.get());
  }

  public static AppModule_ProvideSupabaseApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new AppModule_ProvideSupabaseApiFactory(retrofitProvider);
  }

  public static SupabaseApi provideSupabaseApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSupabaseApi(retrofit));
  }
}
