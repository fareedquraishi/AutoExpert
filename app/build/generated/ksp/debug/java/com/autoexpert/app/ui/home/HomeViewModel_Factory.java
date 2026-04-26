package com.autoexpert.app.ui.home;

import com.autoexpert.app.data.local.dao.AttendanceQueueDao;
import com.autoexpert.app.data.local.dao.MessageDao;
import com.autoexpert.app.data.local.dao.NoticeDao;
import com.autoexpert.app.data.local.dao.PayoutDao;
import com.autoexpert.app.data.local.dao.SaleEntryQueueDao;
import com.autoexpert.app.data.local.dao.TargetDao;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<SessionManager> sessionProvider;

  private final Provider<SaleEntryQueueDao> saleDaoProvider;

  private final Provider<NoticeDao> noticeDaoProvider;

  private final Provider<MessageDao> messageDaoProvider;

  private final Provider<PayoutDao> payoutDaoProvider;

  private final Provider<AttendanceQueueDao> attendanceDaoProvider;

  private final Provider<TargetDao> targetDaoProvider;

  private final Provider<SupabaseApi> apiProvider;

  public HomeViewModel_Factory(Provider<SessionManager> sessionProvider,
      Provider<SaleEntryQueueDao> saleDaoProvider, Provider<NoticeDao> noticeDaoProvider,
      Provider<MessageDao> messageDaoProvider, Provider<PayoutDao> payoutDaoProvider,
      Provider<AttendanceQueueDao> attendanceDaoProvider, Provider<TargetDao> targetDaoProvider,
      Provider<SupabaseApi> apiProvider) {
    this.sessionProvider = sessionProvider;
    this.saleDaoProvider = saleDaoProvider;
    this.noticeDaoProvider = noticeDaoProvider;
    this.messageDaoProvider = messageDaoProvider;
    this.payoutDaoProvider = payoutDaoProvider;
    this.attendanceDaoProvider = attendanceDaoProvider;
    this.targetDaoProvider = targetDaoProvider;
    this.apiProvider = apiProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(sessionProvider.get(), saleDaoProvider.get(), noticeDaoProvider.get(), messageDaoProvider.get(), payoutDaoProvider.get(), attendanceDaoProvider.get(), targetDaoProvider.get(), apiProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<SessionManager> sessionProvider,
      Provider<SaleEntryQueueDao> saleDaoProvider, Provider<NoticeDao> noticeDaoProvider,
      Provider<MessageDao> messageDaoProvider, Provider<PayoutDao> payoutDaoProvider,
      Provider<AttendanceQueueDao> attendanceDaoProvider, Provider<TargetDao> targetDaoProvider,
      Provider<SupabaseApi> apiProvider) {
    return new HomeViewModel_Factory(sessionProvider, saleDaoProvider, noticeDaoProvider, messageDaoProvider, payoutDaoProvider, attendanceDaoProvider, targetDaoProvider, apiProvider);
  }

  public static HomeViewModel newInstance(SessionManager session, SaleEntryQueueDao saleDao,
      NoticeDao noticeDao, MessageDao messageDao, PayoutDao payoutDao,
      AttendanceQueueDao attendanceDao, TargetDao targetDao, SupabaseApi api) {
    return new HomeViewModel(session, saleDao, noticeDao, messageDao, payoutDao, attendanceDao, targetDao, api);
  }
}
