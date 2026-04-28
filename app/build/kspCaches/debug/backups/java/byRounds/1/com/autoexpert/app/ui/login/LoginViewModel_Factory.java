package com.autoexpert.app.ui.login;

import com.autoexpert.app.data.local.dao.BrandAmbassadorDao;
import com.autoexpert.app.data.remote.api.SupabaseApi;
import com.autoexpert.app.util.SessionManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<SupabaseApi> apiProvider;

  private final Provider<BrandAmbassadorDao> baDaoProvider;

  private final Provider<SessionManager> sessionProvider;

  public LoginViewModel_Factory(Provider<SupabaseApi> apiProvider,
      Provider<BrandAmbassadorDao> baDaoProvider, Provider<SessionManager> sessionProvider) {
    this.apiProvider = apiProvider;
    this.baDaoProvider = baDaoProvider;
    this.sessionProvider = sessionProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(apiProvider.get(), baDaoProvider.get(), sessionProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<SupabaseApi> apiProvider,
      Provider<BrandAmbassadorDao> baDaoProvider, Provider<SessionManager> sessionProvider) {
    return new LoginViewModel_Factory(apiProvider, baDaoProvider, sessionProvider);
  }

  public static LoginViewModel newInstance(SupabaseApi api, BrandAmbassadorDao baDao,
      SessionManager session) {
    return new LoginViewModel(api, baDao, session);
  }
}
