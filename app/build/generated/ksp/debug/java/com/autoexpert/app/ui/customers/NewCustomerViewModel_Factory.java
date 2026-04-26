package com.autoexpert.app.ui.customers;

import android.content.Context;
import com.autoexpert.app.data.local.dao.BaCommissionOverrideDao;
import com.autoexpert.app.data.local.dao.CommissionPackageDao;
import com.autoexpert.app.data.local.dao.CompetitorBrandDao;
import com.autoexpert.app.data.local.dao.SaleEntryQueueDao;
import com.autoexpert.app.data.local.dao.SkuDao;
import com.autoexpert.app.data.local.dao.VehicleTypeDao;
import com.autoexpert.app.data.remote.api.SupabaseApi;
import com.autoexpert.app.util.SessionManager;
import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class NewCustomerViewModel_Factory implements Factory<NewCustomerViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<SessionManager> sessionProvider;

  private final Provider<SaleEntryQueueDao> saleQueueDaoProvider;

  private final Provider<SkuDao> skuDaoProvider;

  private final Provider<VehicleTypeDao> vehicleTypeDaoProvider;

  private final Provider<CompetitorBrandDao> competitorBrandDaoProvider;

  private final Provider<SupabaseApi> apiProvider;

  private final Provider<Gson> gsonProvider;

  private final Provider<CommissionPackageDao> commissionPackageDaoProvider;

  private final Provider<BaCommissionOverrideDao> baCommissionOverrideDaoProvider;

  public NewCustomerViewModel_Factory(Provider<Context> contextProvider,
      Provider<SessionManager> sessionProvider, Provider<SaleEntryQueueDao> saleQueueDaoProvider,
      Provider<SkuDao> skuDaoProvider, Provider<VehicleTypeDao> vehicleTypeDaoProvider,
      Provider<CompetitorBrandDao> competitorBrandDaoProvider, Provider<SupabaseApi> apiProvider,
      Provider<Gson> gsonProvider, Provider<CommissionPackageDao> commissionPackageDaoProvider,
      Provider<BaCommissionOverrideDao> baCommissionOverrideDaoProvider) {
    this.contextProvider = contextProvider;
    this.sessionProvider = sessionProvider;
    this.saleQueueDaoProvider = saleQueueDaoProvider;
    this.skuDaoProvider = skuDaoProvider;
    this.vehicleTypeDaoProvider = vehicleTypeDaoProvider;
    this.competitorBrandDaoProvider = competitorBrandDaoProvider;
    this.apiProvider = apiProvider;
    this.gsonProvider = gsonProvider;
    this.commissionPackageDaoProvider = commissionPackageDaoProvider;
    this.baCommissionOverrideDaoProvider = baCommissionOverrideDaoProvider;
  }

  @Override
  public NewCustomerViewModel get() {
    return newInstance(contextProvider.get(), sessionProvider.get(), saleQueueDaoProvider.get(), skuDaoProvider.get(), vehicleTypeDaoProvider.get(), competitorBrandDaoProvider.get(), apiProvider.get(), gsonProvider.get(), commissionPackageDaoProvider.get(), baCommissionOverrideDaoProvider.get());
  }

  public static NewCustomerViewModel_Factory create(Provider<Context> contextProvider,
      Provider<SessionManager> sessionProvider, Provider<SaleEntryQueueDao> saleQueueDaoProvider,
      Provider<SkuDao> skuDaoProvider, Provider<VehicleTypeDao> vehicleTypeDaoProvider,
      Provider<CompetitorBrandDao> competitorBrandDaoProvider, Provider<SupabaseApi> apiProvider,
      Provider<Gson> gsonProvider, Provider<CommissionPackageDao> commissionPackageDaoProvider,
      Provider<BaCommissionOverrideDao> baCommissionOverrideDaoProvider) {
    return new NewCustomerViewModel_Factory(contextProvider, sessionProvider, saleQueueDaoProvider, skuDaoProvider, vehicleTypeDaoProvider, competitorBrandDaoProvider, apiProvider, gsonProvider, commissionPackageDaoProvider, baCommissionOverrideDaoProvider);
  }

  public static NewCustomerViewModel newInstance(Context context, SessionManager session,
      SaleEntryQueueDao saleQueueDao, SkuDao skuDao, VehicleTypeDao vehicleTypeDao,
      CompetitorBrandDao competitorBrandDao, SupabaseApi api, Gson gson,
      CommissionPackageDao commissionPackageDao, BaCommissionOverrideDao baCommissionOverrideDao) {
    return new NewCustomerViewModel(context, session, saleQueueDao, skuDao, vehicleTypeDao, competitorBrandDao, api, gson, commissionPackageDao, baCommissionOverrideDao);
  }
}
