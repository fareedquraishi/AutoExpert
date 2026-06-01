package com.autoexpert.app.ui.summary;

import com.autoexpert.app.data.local.dao.PayoutDao;
import com.autoexpert.app.data.local.dao.SaleEntryQueueDao;
import com.autoexpert.app.data.local.dao.SkuDao;
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
public final class SummaryViewModel_Factory implements Factory<SummaryViewModel> {
  private final Provider<SaleEntryQueueDao> saleDaoProvider;

  private final Provider<PayoutDao> payoutDaoProvider;

  private final Provider<SessionManager> sessionProvider;

  private final Provider<SkuDao> skuDaoProvider;

  public SummaryViewModel_Factory(Provider<SaleEntryQueueDao> saleDaoProvider,
      Provider<PayoutDao> payoutDaoProvider, Provider<SessionManager> sessionProvider,
      Provider<SkuDao> skuDaoProvider) {
    this.saleDaoProvider = saleDaoProvider;
    this.payoutDaoProvider = payoutDaoProvider;
    this.sessionProvider = sessionProvider;
    this.skuDaoProvider = skuDaoProvider;
  }

  @Override
  public SummaryViewModel get() {
    return newInstance(saleDaoProvider.get(), payoutDaoProvider.get(), sessionProvider.get(), skuDaoProvider.get());
  }

  public static SummaryViewModel_Factory create(Provider<SaleEntryQueueDao> saleDaoProvider,
      Provider<PayoutDao> payoutDaoProvider, Provider<SessionManager> sessionProvider,
      Provider<SkuDao> skuDaoProvider) {
    return new SummaryViewModel_Factory(saleDaoProvider, payoutDaoProvider, sessionProvider, skuDaoProvider);
  }

  public static SummaryViewModel newInstance(SaleEntryQueueDao saleDao, PayoutDao payoutDao,
      SessionManager session, SkuDao skuDao) {
    return new SummaryViewModel(saleDao, payoutDao, session, skuDao);
  }
}
