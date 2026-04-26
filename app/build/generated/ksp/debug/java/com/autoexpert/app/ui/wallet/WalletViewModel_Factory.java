package com.autoexpert.app.ui.wallet;

import com.autoexpert.app.data.local.dao.PayoutDao;
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

  public WalletViewModel_Factory(Provider<PayoutDao> payoutDaoProvider,
      Provider<SessionManager> sessionProvider) {
    this.payoutDaoProvider = payoutDaoProvider;
    this.sessionProvider = sessionProvider;
  }

  @Override
  public WalletViewModel get() {
    return newInstance(payoutDaoProvider.get(), sessionProvider.get());
  }

  public static WalletViewModel_Factory create(Provider<PayoutDao> payoutDaoProvider,
      Provider<SessionManager> sessionProvider) {
    return new WalletViewModel_Factory(payoutDaoProvider, sessionProvider);
  }

  public static WalletViewModel newInstance(PayoutDao payoutDao, SessionManager session) {
    return new WalletViewModel(payoutDao, session);
  }
}
