package com.autoexpert.app;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AutoExpertApp_MembersInjector implements MembersInjector<AutoExpertApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public AutoExpertApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<AutoExpertApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new AutoExpertApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(AutoExpertApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.autoexpert.app.AutoExpertApp.workerFactory")
  public static void injectWorkerFactory(AutoExpertApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
