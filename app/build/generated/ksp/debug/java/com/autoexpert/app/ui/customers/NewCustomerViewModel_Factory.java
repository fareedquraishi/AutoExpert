package com.autoexpert.app.ui.customers;

import android.content.Context;
import com.autoexpert.app.data.local.dao.BaCommissionOverrideDao;
import com.autoexpert.app.data.local.dao.CommissionPackageDao;
import com.autoexpert.app.data.local.dao.CommissionTierDao;
import com.autoexpert.app.data.local.dao.CompetitorBrandDao;
import com.autoexpert.app.data.local.dao.SaleEntryQueueDao;
import com.autoexpert.app.data.local.dao.SkuDao;
import com.autoexpert.app.data.local.dao.VehicleTypeDao;
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

  private final Provider<SkuDao> skuDaoProvider;

  private final Provider<VehicleTypeDao> vehicleTypeDaoProvider;

  private final Provider<CompetitorBrandDao> competitorBrandDaoProvider;

  private final Provider<CommissionPackageDao> commissionPackageDaoProvider;

  private final Provider<CommissionTierDao> commissionTierDaoProvider;

  private final Provider<BaCommissionOverrideDao> baCommissionOverrideDaoProvider;

  private final Provider<SaleEntryQueueDao> saleQueueDaoProvider;

  private final Provider<Gson> gsonProvider;

  public NewCustomerViewModel_Factory(Provider<Context> contextProvider,
      Provider<SessionManager> sessionProvider, Provider<SkuDao> skuDaoProvider,
      Provider<VehicleTypeDao> vehicleTypeDaoProvider,
      Provider<CompetitorBrandDao> competitorBrandDaoProvider,
      Provider<CommissionPackageDao> commissionPackageDaoProvider,
      Provider<CommissionTierDao> commissionTierDaoProvider,
      Provider<BaCommissionOverrideDao> baCommissionOverrideDaoProvider,
      Provider<SaleEntryQueueDao> saleQueueDaoProvider, Provider<Gson> gsonProvider) {
    this.contextProvider = contextProvider;
    this.sessionProvider = sessionProvider;
    this.skuDaoProvider = skuDaoProvider;
    this.vehicleTypeDaoProvider = vehicleTypeDaoProvider;
    this.competitorBrandDaoProvider = competitorBrandDaoProvider;
    this.commissionPackageDaoProvider = commissionPackageDaoProvider;
    this.commissionTierDaoProvider = commissionTierDaoProvider;
    this.baCommissionOverrideDaoProvider = baCommissionOverrideDaoProvider;
    this.saleQueueDaoProvider = saleQueueDaoProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public NewCustomerViewModel get() {
    return newInstance(contextProvider.get(), sessionProvider.get(), skuDaoProvider.get(), vehicleTypeDaoProvider.get(), competitorBrandDaoProvider.get(), commissionPackageDaoProvider.get(), commissionTierDaoProvider.get(), baCommissionOverrideDaoProvider.get(), saleQueueDaoProvider.get(), gsonProvider.get());
  }

  public static NewCustomerViewModel_Factory create(Provider<Context> contextProvider,
      Provider<SessionManager> sessionProvider, Provider<SkuDao> skuDaoProvider,
      Provider<VehicleTypeDao> vehicleTypeDaoProvider,
      Provider<CompetitorBrandDao> competitorBrandDaoProvider,
      Provider<CommissionPackageDao> commissionPackageDaoProvider,
      Provider<CommissionTierDao> commissionTierDaoProvider,
      Provider<BaCommissionOverrideDao> baCommissionOverrideDaoProvider,
      Provider<SaleEntryQueueDao> saleQueueDaoProvider, Provider<Gson> gsonProvider) {
    return new NewCustomerViewModel_Factory(contextProvider, sessionProvider, skuDaoProvider, vehicleTypeDaoProvider, competitorBrandDaoProvider, commissionPackageDaoProvider, commissionTierDaoProvider, baCommissionOverrideDaoProvider, saleQueueDaoProvider, gsonProvider);
  }

  public static NewCustomerViewModel newInstance(Context context, SessionManager session,
      SkuDao skuDao, VehicleTypeDao vehicleTypeDao, CompetitorBrandDao competitorBrandDao,
      CommissionPackageDao commissionPackageDao, CommissionTierDao commissionTierDao,
      BaCommissionOverrideDao baCommissionOverrideDao, SaleEntryQueueDao saleQueueDao, Gson gson) {
    return new NewCustomerViewModel(context, session, skuDao, vehicleTypeDao, competitorBrandDao, commissionPackageDao, commissionTierDao, baCommissionOverrideDao, saleQueueDao, gson);
  }
}
