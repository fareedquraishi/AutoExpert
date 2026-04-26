package com.autoexpert.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.autoexpert.app.data.local.dao.AttendanceQueueDao;
import com.autoexpert.app.data.local.dao.BaCommissionOverrideDao;
import com.autoexpert.app.data.local.dao.BrandAmbassadorDao;
import com.autoexpert.app.data.local.dao.CommissionPackageDao;
import com.autoexpert.app.data.local.dao.CommissionTierDao;
import com.autoexpert.app.data.local.dao.CompetitorBrandDao;
import com.autoexpert.app.data.local.dao.LeaveRequestDao;
import com.autoexpert.app.data.local.dao.MessageDao;
import com.autoexpert.app.data.local.dao.NoticeDao;
import com.autoexpert.app.data.local.dao.PayoutDao;
import com.autoexpert.app.data.local.dao.SaleEntryQueueDao;
import com.autoexpert.app.data.local.dao.SkuDao;
import com.autoexpert.app.data.local.dao.TargetDao;
import com.autoexpert.app.data.local.dao.VehicleTypeDao;
import com.autoexpert.app.data.local.db.AppDatabase;
import com.autoexpert.app.data.remote.api.SupabaseApi;
import com.autoexpert.app.di.AppModule_ProvideAttendanceQueueDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideBaCommissionOverrideDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideBaDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideCommissionPackageDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideCommissionTierDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideCompetitorBrandDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideDatabaseFactory;
import com.autoexpert.app.di.AppModule_ProvideLeaveRequestDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideMessageDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideNoticeDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideOkHttpFactory;
import com.autoexpert.app.di.AppModule_ProvidePayoutDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideRetrofitFactory;
import com.autoexpert.app.di.AppModule_ProvideSaleEntryQueueDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideSkuDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideSupabaseApiFactory;
import com.autoexpert.app.di.AppModule_ProvideTargetDaoFactory;
import com.autoexpert.app.di.AppModule_ProvideVehicleTypeDaoFactory;
import com.autoexpert.app.di.GsonModule_ProvideGsonFactory;
import com.autoexpert.app.di.LocationModule_ProvideFusedLocationClientFactory;
import com.autoexpert.app.di.LocationModule_ProvideGeofencingClientFactory;
import com.autoexpert.app.service.AttendanceGeofenceService;
import com.autoexpert.app.service.AttendanceGeofenceService_MembersInjector;
import com.autoexpert.app.service.AutoExpertFirebaseService;
import com.autoexpert.app.service.AutoExpertFirebaseService_MembersInjector;
import com.autoexpert.app.service.GeofenceBroadcastReceiver;
import com.autoexpert.app.service.GeofenceBroadcastReceiver_MembersInjector;
import com.autoexpert.app.service.SyncWorker;
import com.autoexpert.app.service.SyncWorker_AssistedFactory;
import com.autoexpert.app.ui.customers.CustomerListViewModel;
import com.autoexpert.app.ui.customers.CustomerListViewModel_HiltModules;
import com.autoexpert.app.ui.customers.NewCustomerViewModel;
import com.autoexpert.app.ui.customers.NewCustomerViewModel_HiltModules;
import com.autoexpert.app.ui.home.HomeViewModel;
import com.autoexpert.app.ui.home.HomeViewModel_HiltModules;
import com.autoexpert.app.ui.login.LoginViewModel;
import com.autoexpert.app.ui.login.LoginViewModel_HiltModules;
import com.autoexpert.app.ui.messaging.MessagingViewModel;
import com.autoexpert.app.ui.messaging.MessagingViewModel_HiltModules;
import com.autoexpert.app.ui.notices.NoticesViewModel;
import com.autoexpert.app.ui.notices.NoticesViewModel_HiltModules;
import com.autoexpert.app.ui.profile.ProfileViewModel;
import com.autoexpert.app.ui.profile.ProfileViewModel_HiltModules;
import com.autoexpert.app.ui.wallet.WalletViewModel;
import com.autoexpert.app.ui.wallet.WalletViewModel_HiltModules;
import com.autoexpert.app.util.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class DaggerAutoExpertApp_HiltComponents_SingletonC {
  private DaggerAutoExpertApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public AutoExpertApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements AutoExpertApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public AutoExpertApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements AutoExpertApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public AutoExpertApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements AutoExpertApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public AutoExpertApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements AutoExpertApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AutoExpertApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements AutoExpertApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AutoExpertApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements AutoExpertApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public AutoExpertApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements AutoExpertApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public AutoExpertApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends AutoExpertApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends AutoExpertApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends AutoExpertApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends AutoExpertApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(8).put(LazyClassKeyProvider.com_autoexpert_app_ui_customers_CustomerListViewModel, CustomerListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_autoexpert_app_ui_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_autoexpert_app_ui_login_LoginViewModel, LoginViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_autoexpert_app_ui_messaging_MessagingViewModel, MessagingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_autoexpert_app_ui_customers_NewCustomerViewModel, NewCustomerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_autoexpert_app_ui_notices_NoticesViewModel, NoticesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_autoexpert_app_ui_profile_ProfileViewModel, ProfileViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_autoexpert_app_ui_wallet_WalletViewModel, WalletViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_autoexpert_app_ui_wallet_WalletViewModel = "com.autoexpert.app.ui.wallet.WalletViewModel";

      static String com_autoexpert_app_ui_messaging_MessagingViewModel = "com.autoexpert.app.ui.messaging.MessagingViewModel";

      static String com_autoexpert_app_ui_notices_NoticesViewModel = "com.autoexpert.app.ui.notices.NoticesViewModel";

      static String com_autoexpert_app_ui_customers_CustomerListViewModel = "com.autoexpert.app.ui.customers.CustomerListViewModel";

      static String com_autoexpert_app_ui_customers_NewCustomerViewModel = "com.autoexpert.app.ui.customers.NewCustomerViewModel";

      static String com_autoexpert_app_ui_login_LoginViewModel = "com.autoexpert.app.ui.login.LoginViewModel";

      static String com_autoexpert_app_ui_profile_ProfileViewModel = "com.autoexpert.app.ui.profile.ProfileViewModel";

      static String com_autoexpert_app_ui_home_HomeViewModel = "com.autoexpert.app.ui.home.HomeViewModel";

      @KeepFieldType
      WalletViewModel com_autoexpert_app_ui_wallet_WalletViewModel2;

      @KeepFieldType
      MessagingViewModel com_autoexpert_app_ui_messaging_MessagingViewModel2;

      @KeepFieldType
      NoticesViewModel com_autoexpert_app_ui_notices_NoticesViewModel2;

      @KeepFieldType
      CustomerListViewModel com_autoexpert_app_ui_customers_CustomerListViewModel2;

      @KeepFieldType
      NewCustomerViewModel com_autoexpert_app_ui_customers_NewCustomerViewModel2;

      @KeepFieldType
      LoginViewModel com_autoexpert_app_ui_login_LoginViewModel2;

      @KeepFieldType
      ProfileViewModel com_autoexpert_app_ui_profile_ProfileViewModel2;

      @KeepFieldType
      HomeViewModel com_autoexpert_app_ui_home_HomeViewModel2;
    }
  }

  private static final class ViewModelCImpl extends AutoExpertApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<CustomerListViewModel> customerListViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LoginViewModel> loginViewModelProvider;

    private Provider<MessagingViewModel> messagingViewModelProvider;

    private Provider<NewCustomerViewModel> newCustomerViewModelProvider;

    private Provider<NoticesViewModel> noticesViewModelProvider;

    private Provider<ProfileViewModel> profileViewModelProvider;

    private Provider<WalletViewModel> walletViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.customerListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.loginViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.messagingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.newCustomerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.noticesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.walletViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(8).put(LazyClassKeyProvider.com_autoexpert_app_ui_customers_CustomerListViewModel, ((Provider) customerListViewModelProvider)).put(LazyClassKeyProvider.com_autoexpert_app_ui_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_autoexpert_app_ui_login_LoginViewModel, ((Provider) loginViewModelProvider)).put(LazyClassKeyProvider.com_autoexpert_app_ui_messaging_MessagingViewModel, ((Provider) messagingViewModelProvider)).put(LazyClassKeyProvider.com_autoexpert_app_ui_customers_NewCustomerViewModel, ((Provider) newCustomerViewModelProvider)).put(LazyClassKeyProvider.com_autoexpert_app_ui_notices_NoticesViewModel, ((Provider) noticesViewModelProvider)).put(LazyClassKeyProvider.com_autoexpert_app_ui_profile_ProfileViewModel, ((Provider) profileViewModelProvider)).put(LazyClassKeyProvider.com_autoexpert_app_ui_wallet_WalletViewModel, ((Provider) walletViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_autoexpert_app_ui_customers_NewCustomerViewModel = "com.autoexpert.app.ui.customers.NewCustomerViewModel";

      static String com_autoexpert_app_ui_messaging_MessagingViewModel = "com.autoexpert.app.ui.messaging.MessagingViewModel";

      static String com_autoexpert_app_ui_notices_NoticesViewModel = "com.autoexpert.app.ui.notices.NoticesViewModel";

      static String com_autoexpert_app_ui_customers_CustomerListViewModel = "com.autoexpert.app.ui.customers.CustomerListViewModel";

      static String com_autoexpert_app_ui_login_LoginViewModel = "com.autoexpert.app.ui.login.LoginViewModel";

      static String com_autoexpert_app_ui_home_HomeViewModel = "com.autoexpert.app.ui.home.HomeViewModel";

      static String com_autoexpert_app_ui_profile_ProfileViewModel = "com.autoexpert.app.ui.profile.ProfileViewModel";

      static String com_autoexpert_app_ui_wallet_WalletViewModel = "com.autoexpert.app.ui.wallet.WalletViewModel";

      @KeepFieldType
      NewCustomerViewModel com_autoexpert_app_ui_customers_NewCustomerViewModel2;

      @KeepFieldType
      MessagingViewModel com_autoexpert_app_ui_messaging_MessagingViewModel2;

      @KeepFieldType
      NoticesViewModel com_autoexpert_app_ui_notices_NoticesViewModel2;

      @KeepFieldType
      CustomerListViewModel com_autoexpert_app_ui_customers_CustomerListViewModel2;

      @KeepFieldType
      LoginViewModel com_autoexpert_app_ui_login_LoginViewModel2;

      @KeepFieldType
      HomeViewModel com_autoexpert_app_ui_home_HomeViewModel2;

      @KeepFieldType
      ProfileViewModel com_autoexpert_app_ui_profile_ProfileViewModel2;

      @KeepFieldType
      WalletViewModel com_autoexpert_app_ui_wallet_WalletViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.autoexpert.app.ui.customers.CustomerListViewModel 
          return (T) new CustomerListViewModel(singletonCImpl.saleEntryQueueDao(), singletonCImpl.sessionManagerProvider.get());

          case 1: // com.autoexpert.app.ui.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.sessionManagerProvider.get(), singletonCImpl.saleEntryQueueDao(), singletonCImpl.noticeDao(), singletonCImpl.messageDao(), singletonCImpl.payoutDao(), singletonCImpl.attendanceQueueDao(), singletonCImpl.targetDao(), singletonCImpl.provideSupabaseApiProvider.get());

          case 2: // com.autoexpert.app.ui.login.LoginViewModel 
          return (T) new LoginViewModel(singletonCImpl.brandAmbassadorDao(), singletonCImpl.provideSupabaseApiProvider.get(), singletonCImpl.sessionManagerProvider.get());

          case 3: // com.autoexpert.app.ui.messaging.MessagingViewModel 
          return (T) new MessagingViewModel(singletonCImpl.messageDao(), singletonCImpl.provideSupabaseApiProvider.get(), singletonCImpl.sessionManagerProvider.get());

          case 4: // com.autoexpert.app.ui.customers.NewCustomerViewModel 
          return (T) new NewCustomerViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.sessionManagerProvider.get(), singletonCImpl.saleEntryQueueDao(), singletonCImpl.skuDao(), singletonCImpl.vehicleTypeDao(), singletonCImpl.competitorBrandDao(), singletonCImpl.provideSupabaseApiProvider.get(), singletonCImpl.provideGsonProvider.get(), singletonCImpl.commissionPackageDao(), singletonCImpl.baCommissionOverrideDao());

          case 5: // com.autoexpert.app.ui.notices.NoticesViewModel 
          return (T) new NoticesViewModel(singletonCImpl.noticeDao());

          case 6: // com.autoexpert.app.ui.profile.ProfileViewModel 
          return (T) new ProfileViewModel(singletonCImpl.sessionManagerProvider.get(), singletonCImpl.leaveRequestDao(), singletonCImpl.attendanceQueueDao());

          case 7: // com.autoexpert.app.ui.wallet.WalletViewModel 
          return (T) new WalletViewModel(singletonCImpl.payoutDao(), singletonCImpl.sessionManagerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends AutoExpertApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends AutoExpertApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectAttendanceGeofenceService(
        AttendanceGeofenceService attendanceGeofenceService) {
      injectAttendanceGeofenceService2(attendanceGeofenceService);
    }

    @Override
    public void injectAutoExpertFirebaseService(
        AutoExpertFirebaseService autoExpertFirebaseService) {
      injectAutoExpertFirebaseService2(autoExpertFirebaseService);
    }

    @CanIgnoreReturnValue
    private AttendanceGeofenceService injectAttendanceGeofenceService2(
        AttendanceGeofenceService instance) {
      AttendanceGeofenceService_MembersInjector.injectFusedLocationClient(instance, singletonCImpl.provideFusedLocationClientProvider.get());
      AttendanceGeofenceService_MembersInjector.injectGeofencingClient(instance, singletonCImpl.provideGeofencingClientProvider.get());
      AttendanceGeofenceService_MembersInjector.injectSessionManager(instance, singletonCImpl.sessionManagerProvider.get());
      AttendanceGeofenceService_MembersInjector.injectAttendanceDao(instance, singletonCImpl.attendanceQueueDao());
      return instance;
    }

    @CanIgnoreReturnValue
    private AutoExpertFirebaseService injectAutoExpertFirebaseService2(
        AutoExpertFirebaseService instance) {
      AutoExpertFirebaseService_MembersInjector.injectSessionManager(instance, singletonCImpl.sessionManagerProvider.get());
      AutoExpertFirebaseService_MembersInjector.injectMessageDao(instance, singletonCImpl.messageDao());
      return instance;
    }
  }

  private static final class SingletonCImpl extends AutoExpertApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<OkHttpClient> provideOkHttpProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<SupabaseApi> provideSupabaseApiProvider;

    private Provider<SessionManager> sessionManagerProvider;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<Gson> provideGsonProvider;

    private Provider<SyncWorker_AssistedFactory> syncWorker_AssistedFactoryProvider;

    private Provider<FusedLocationProviderClient> provideFusedLocationClientProvider;

    private Provider<GeofencingClient> provideGeofencingClientProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private SaleEntryQueueDao saleEntryQueueDao() {
      return AppModule_ProvideSaleEntryQueueDaoFactory.provideSaleEntryQueueDao(provideDatabaseProvider.get());
    }

    private AttendanceQueueDao attendanceQueueDao() {
      return AppModule_ProvideAttendanceQueueDaoFactory.provideAttendanceQueueDao(provideDatabaseProvider.get());
    }

    private SkuDao skuDao() {
      return AppModule_ProvideSkuDaoFactory.provideSkuDao(provideDatabaseProvider.get());
    }

    private VehicleTypeDao vehicleTypeDao() {
      return AppModule_ProvideVehicleTypeDaoFactory.provideVehicleTypeDao(provideDatabaseProvider.get());
    }

    private CompetitorBrandDao competitorBrandDao() {
      return AppModule_ProvideCompetitorBrandDaoFactory.provideCompetitorBrandDao(provideDatabaseProvider.get());
    }

    private CommissionPackageDao commissionPackageDao() {
      return AppModule_ProvideCommissionPackageDaoFactory.provideCommissionPackageDao(provideDatabaseProvider.get());
    }

    private CommissionTierDao commissionTierDao() {
      return AppModule_ProvideCommissionTierDaoFactory.provideCommissionTierDao(provideDatabaseProvider.get());
    }

    private BaCommissionOverrideDao baCommissionOverrideDao() {
      return AppModule_ProvideBaCommissionOverrideDaoFactory.provideBaCommissionOverrideDao(provideDatabaseProvider.get());
    }

    private NoticeDao noticeDao() {
      return AppModule_ProvideNoticeDaoFactory.provideNoticeDao(provideDatabaseProvider.get());
    }

    private MessageDao messageDao() {
      return AppModule_ProvideMessageDaoFactory.provideMessageDao(provideDatabaseProvider.get());
    }

    private PayoutDao payoutDao() {
      return AppModule_ProvidePayoutDaoFactory.providePayoutDao(provideDatabaseProvider.get());
    }

    private LeaveRequestDao leaveRequestDao() {
      return AppModule_ProvideLeaveRequestDaoFactory.provideLeaveRequestDao(provideDatabaseProvider.get());
    }

    private TargetDao targetDao() {
      return AppModule_ProvideTargetDaoFactory.provideTargetDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return Collections.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>singletonMap("com.autoexpert.app.service.SyncWorker", ((Provider) syncWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private BrandAmbassadorDao brandAmbassadorDao() {
      return AppModule_ProvideBaDaoFactory.provideBaDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideOkHttpProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 3));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 2));
      this.provideSupabaseApiProvider = DoubleCheck.provider(new SwitchingProvider<SupabaseApi>(singletonCImpl, 1));
      this.sessionManagerProvider = DoubleCheck.provider(new SwitchingProvider<SessionManager>(singletonCImpl, 4));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 5));
      this.provideGsonProvider = DoubleCheck.provider(new SwitchingProvider<Gson>(singletonCImpl, 6));
      this.syncWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<SyncWorker_AssistedFactory>(singletonCImpl, 0));
      this.provideFusedLocationClientProvider = DoubleCheck.provider(new SwitchingProvider<FusedLocationProviderClient>(singletonCImpl, 7));
      this.provideGeofencingClientProvider = DoubleCheck.provider(new SwitchingProvider<GeofencingClient>(singletonCImpl, 8));
    }

    @Override
    public void injectAutoExpertApp(AutoExpertApp autoExpertApp) {
      injectAutoExpertApp2(autoExpertApp);
    }

    @Override
    public void injectGeofenceBroadcastReceiver(
        GeofenceBroadcastReceiver geofenceBroadcastReceiver) {
      injectGeofenceBroadcastReceiver2(geofenceBroadcastReceiver);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private AutoExpertApp injectAutoExpertApp2(AutoExpertApp instance) {
      AutoExpertApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    @CanIgnoreReturnValue
    private GeofenceBroadcastReceiver injectGeofenceBroadcastReceiver2(
        GeofenceBroadcastReceiver instance) {
      GeofenceBroadcastReceiver_MembersInjector.injectAttendanceDao(instance, attendanceQueueDao());
      GeofenceBroadcastReceiver_MembersInjector.injectSessionManager(instance, sessionManagerProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.autoexpert.app.service.SyncWorker_AssistedFactory 
          return (T) new SyncWorker_AssistedFactory() {
            @Override
            public SyncWorker create(Context ctx, WorkerParameters params) {
              return new SyncWorker(ctx, params, singletonCImpl.provideSupabaseApiProvider.get(), singletonCImpl.sessionManagerProvider.get(), singletonCImpl.saleEntryQueueDao(), singletonCImpl.attendanceQueueDao(), singletonCImpl.skuDao(), singletonCImpl.vehicleTypeDao(), singletonCImpl.competitorBrandDao(), singletonCImpl.commissionPackageDao(), singletonCImpl.commissionTierDao(), singletonCImpl.baCommissionOverrideDao(), singletonCImpl.noticeDao(), singletonCImpl.messageDao(), singletonCImpl.payoutDao(), singletonCImpl.leaveRequestDao(), singletonCImpl.targetDao(), singletonCImpl.provideGsonProvider.get());
            }
          };

          case 1: // com.autoexpert.app.data.remote.api.SupabaseApi 
          return (T) AppModule_ProvideSupabaseApiFactory.provideSupabaseApi(singletonCImpl.provideRetrofitProvider.get());

          case 2: // retrofit2.Retrofit 
          return (T) AppModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpProvider.get());

          case 3: // okhttp3.OkHttpClient 
          return (T) AppModule_ProvideOkHttpFactory.provideOkHttp();

          case 4: // com.autoexpert.app.util.SessionManager 
          return (T) new SessionManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.autoexpert.app.data.local.db.AppDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.google.gson.Gson 
          return (T) GsonModule_ProvideGsonFactory.provideGson();

          case 7: // com.google.android.gms.location.FusedLocationProviderClient 
          return (T) LocationModule_ProvideFusedLocationClientFactory.provideFusedLocationClient(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.google.android.gms.location.GeofencingClient 
          return (T) LocationModule_ProvideGeofencingClientFactory.provideGeofencingClient(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
