package com.autoexpert.app.ui.customers;

import com.autoexpert.app.data.local.dao.SaleEntryQueueDao;
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
public final class CustomerListViewModel_Factory implements Factory<CustomerListViewModel> {
  private final Provider<SaleEntryQueueDao> saleDaoProvider;

  private final Provider<SessionManager> sessionProvider;

  public CustomerListViewModel_Factory(Provider<SaleEntryQueueDao> saleDaoProvider,
      Provider<SessionManager> sessionProvider) {
    this.saleDaoProvider = saleDaoProvider;
    this.sessionProvider = sessionProvider;
  }

  @Override
  public CustomerListViewModel get() {
    return newInstance(saleDaoProvider.get(), sessionProvider.get());
  }

  public static CustomerListViewModel_Factory create(Provider<SaleEntryQueueDao> saleDaoProvider,
      Provider<SessionManager> sessionProvider) {
    return new CustomerListViewModel_Factory(saleDaoProvider, sessionProvider);
  }

  public static CustomerListViewModel newInstance(SaleEntryQueueDao saleDao,
      SessionManager session) {
    return new CustomerListViewModel(saleDao, session);
  }
}
