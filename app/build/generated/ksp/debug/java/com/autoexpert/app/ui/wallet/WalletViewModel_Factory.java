package com.autoexpert.app.ui.wallet;

import com.autoexpert.app.data.local.dao.PayoutDao;
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
public final class WalletViewModel_Factory implements Factory<WalletViewModel> {
  private final Provider<PayoutDao> payoutDaoProvider;

  private final Provider<SessionManager> sessionProvider;

  private final Provider<SupabaseApi> apiProvider;

  public WalletViewModel_Factory(Provider<PayoutDao> payoutDaoProvider,
      Provider<SessionManager> sessionProvider, Provider<SupabaseApi> apiProvider) {
    this.payoutDaoProvider = payoutDaoProvider;
    this.sessionProvider = sessionProvider;
    this.apiProvider = apiProvider;
  }

  @Override
  public WalletViewModel get() {
    return newInstance(payoutDaoProvider.get(), sessionProvider.get(), apiProvider.get());
  }

  public static WalletViewModel_Factory create(Provider<PayoutDao> payoutDaoProvider,
      Provider<SessionManager> sessionProvider, Provider<SupabaseApi> apiProvider) {
    return new WalletViewModel_Factory(payoutDaoProvider, sessionProvider, apiProvider);
  }

  public static WalletViewModel newInstance(PayoutDao payoutDao, SessionManager session,
      SupabaseApi api) {
    return new WalletViewModel(payoutDao, session, api);
  }
}
