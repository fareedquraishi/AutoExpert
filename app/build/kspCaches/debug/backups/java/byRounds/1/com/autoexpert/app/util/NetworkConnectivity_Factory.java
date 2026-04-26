package com.autoexpert.app.util;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class NetworkConnectivity_Factory implements Factory<NetworkConnectivity> {
  private final Provider<Context> contextProvider;

  public NetworkConnectivity_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public NetworkConnectivity get() {
    return newInstance(contextProvider.get());
  }

  public static NetworkConnectivity_Factory create(Provider<Context> contextProvider) {
    return new NetworkConnectivity_Factory(contextProvider);
  }

  public static NetworkConnectivity newInstance(Context context) {
    return new NetworkConnectivity(context);
  }
}
