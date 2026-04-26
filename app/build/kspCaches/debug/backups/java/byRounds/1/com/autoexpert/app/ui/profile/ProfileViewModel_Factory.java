package com.autoexpert.app.ui.profile;

import com.autoexpert.app.data.local.dao.AttendanceQueueDao;
import com.autoexpert.app.data.local.dao.LeaveRequestDao;
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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<SessionManager> sessionProvider;

  private final Provider<LeaveRequestDao> leaveDaoProvider;

  private final Provider<AttendanceQueueDao> attendanceDaoProvider;

  public ProfileViewModel_Factory(Provider<SessionManager> sessionProvider,
      Provider<LeaveRequestDao> leaveDaoProvider,
      Provider<AttendanceQueueDao> attendanceDaoProvider) {
    this.sessionProvider = sessionProvider;
    this.leaveDaoProvider = leaveDaoProvider;
    this.attendanceDaoProvider = attendanceDaoProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(sessionProvider.get(), leaveDaoProvider.get(), attendanceDaoProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<SessionManager> sessionProvider,
      Provider<LeaveRequestDao> leaveDaoProvider,
      Provider<AttendanceQueueDao> attendanceDaoProvider) {
    return new ProfileViewModel_Factory(sessionProvider, leaveDaoProvider, attendanceDaoProvider);
  }

  public static ProfileViewModel newInstance(SessionManager session, LeaveRequestDao leaveDao,
      AttendanceQueueDao attendanceDao) {
    return new ProfileViewModel(session, leaveDao, attendanceDao);
  }
}
