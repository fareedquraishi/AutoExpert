package com.autoexpert.app.service;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.autoexpert.app.data.local.dao.AttendanceQueueDao;
import com.autoexpert.app.data.local.dao.BaCommissionOverrideDao;
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
import com.autoexpert.app.data.remote.api.SupabaseApi;
import com.autoexpert.app.util.SessionManager;
import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
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
public final class SyncWorker_Factory {
  private final Provider<SupabaseApi> apiProvider;

  private final Provider<SessionManager> sessionProvider;

  private final Provider<SaleEntryQueueDao> saleDaoProvider;

  private final Provider<AttendanceQueueDao> attendanceDaoProvider;

  private final Provider<SkuDao> skuDaoProvider;

  private final Provider<VehicleTypeDao> vehicleTypeDaoProvider;

  private final Provider<CompetitorBrandDao> competitorBrandDaoProvider;

  private final Provider<CommissionPackageDao> commissionPackageDaoProvider;

  private final Provider<CommissionTierDao> commissionTierDaoProvider;

  private final Provider<BaCommissionOverrideDao> baCommissionOverrideDaoProvider;

  private final Provider<NoticeDao> noticeDaoProvider;

  private final Provider<MessageDao> messageDaoProvider;

  private final Provider<PayoutDao> payoutDaoProvider;

  private final Provider<LeaveRequestDao> leaveDaoProvider;

  private final Provider<TargetDao> targetDaoProvider;

  private final Provider<Gson> gsonProvider;

  public SyncWorker_Factory(Provider<SupabaseApi> apiProvider,
      Provider<SessionManager> sessionProvider, Provider<SaleEntryQueueDao> saleDaoProvider,
      Provider<AttendanceQueueDao> attendanceDaoProvider, Provider<SkuDao> skuDaoProvider,
      Provider<VehicleTypeDao> vehicleTypeDaoProvider,
      Provider<CompetitorBrandDao> competitorBrandDaoProvider,
      Provider<CommissionPackageDao> commissionPackageDaoProvider,
      Provider<CommissionTierDao> commissionTierDaoProvider,
      Provider<BaCommissionOverrideDao> baCommissionOverrideDaoProvider,
      Provider<NoticeDao> noticeDaoProvider, Provider<MessageDao> messageDaoProvider,
      Provider<PayoutDao> payoutDaoProvider, Provider<LeaveRequestDao> leaveDaoProvider,
      Provider<TargetDao> targetDaoProvider, Provider<Gson> gsonProvider) {
    this.apiProvider = apiProvider;
    this.sessionProvider = sessionProvider;
    this.saleDaoProvider = saleDaoProvider;
    this.attendanceDaoProvider = attendanceDaoProvider;
    this.skuDaoProvider = skuDaoProvider;
    this.vehicleTypeDaoProvider = vehicleTypeDaoProvider;
    this.competitorBrandDaoProvider = competitorBrandDaoProvider;
    this.commissionPackageDaoProvider = commissionPackageDaoProvider;
    this.commissionTierDaoProvider = commissionTierDaoProvider;
    this.baCommissionOverrideDaoProvider = baCommissionOverrideDaoProvider;
    this.noticeDaoProvider = noticeDaoProvider;
    this.messageDaoProvider = messageDaoProvider;
    this.payoutDaoProvider = payoutDaoProvider;
    this.leaveDaoProvider = leaveDaoProvider;
    this.targetDaoProvider = targetDaoProvider;
    this.gsonProvider = gsonProvider;
  }

  public SyncWorker get(Context ctx, WorkerParameters params) {
    return newInstance(ctx, params, apiProvider.get(), sessionProvider.get(), saleDaoProvider.get(), attendanceDaoProvider.get(), skuDaoProvider.get(), vehicleTypeDaoProvider.get(), competitorBrandDaoProvider.get(), commissionPackageDaoProvider.get(), commissionTierDaoProvider.get(), baCommissionOverrideDaoProvider.get(), noticeDaoProvider.get(), messageDaoProvider.get(), payoutDaoProvider.get(), leaveDaoProvider.get(), targetDaoProvider.get(), gsonProvider.get());
  }

  public static SyncWorker_Factory create(Provider<SupabaseApi> apiProvider,
      Provider<SessionManager> sessionProvider, Provider<SaleEntryQueueDao> saleDaoProvider,
      Provider<AttendanceQueueDao> attendanceDaoProvider, Provider<SkuDao> skuDaoProvider,
      Provider<VehicleTypeDao> vehicleTypeDaoProvider,
      Provider<CompetitorBrandDao> competitorBrandDaoProvider,
      Provider<CommissionPackageDao> commissionPackageDaoProvider,
      Provider<CommissionTierDao> commissionTierDaoProvider,
      Provider<BaCommissionOverrideDao> baCommissionOverrideDaoProvider,
      Provider<NoticeDao> noticeDaoProvider, Provider<MessageDao> messageDaoProvider,
      Provider<PayoutDao> payoutDaoProvider, Provider<LeaveRequestDao> leaveDaoProvider,
      Provider<TargetDao> targetDaoProvider, Provider<Gson> gsonProvider) {
    return new SyncWorker_Factory(apiProvider, sessionProvider, saleDaoProvider, attendanceDaoProvider, skuDaoProvider, vehicleTypeDaoProvider, competitorBrandDaoProvider, commissionPackageDaoProvider, commissionTierDaoProvider, baCommissionOverrideDaoProvider, noticeDaoProvider, messageDaoProvider, payoutDaoProvider, leaveDaoProvider, targetDaoProvider, gsonProvider);
  }

  public static SyncWorker newInstance(Context ctx, WorkerParameters params, SupabaseApi api,
      SessionManager session, SaleEntryQueueDao saleDao, AttendanceQueueDao attendanceDao,
      SkuDao skuDao, VehicleTypeDao vehicleTypeDao, CompetitorBrandDao competitorBrandDao,
      CommissionPackageDao commissionPackageDao, CommissionTierDao commissionTierDao,
      BaCommissionOverrideDao baCommissionOverrideDao, NoticeDao noticeDao, MessageDao messageDao,
      PayoutDao payoutDao, LeaveRequestDao leaveDao, TargetDao targetDao, Gson gson) {
    return new SyncWorker(ctx, params, api, session, saleDao, attendanceDao, skuDao, vehicleTypeDao, competitorBrandDao, commissionPackageDao, commissionTierDao, baCommissionOverrideDao, noticeDao, messageDao, payoutDao, leaveDao, targetDao, gson);
  }
}
