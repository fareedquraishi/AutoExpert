package com.autoexpert.app.service;

import com.autoexpert.app.data.local.dao.AttendanceQueueDao;
import com.autoexpert.app.util.SessionManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class GeofenceBroadcastReceiver_MembersInjector implements MembersInjector<GeofenceBroadcastReceiver> {
  private final Provider<AttendanceQueueDao> attendanceDaoProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  public GeofenceBroadcastReceiver_MembersInjector(
      Provider<AttendanceQueueDao> attendanceDaoProvider,
      Provider<SessionManager> sessionManagerProvider) {
    this.attendanceDaoProvider = attendanceDaoProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  public static MembersInjector<GeofenceBroadcastReceiver> create(
      Provider<AttendanceQueueDao> attendanceDaoProvider,
      Provider<SessionManager> sessionManagerProvider) {
    return new GeofenceBroadcastReceiver_MembersInjector(attendanceDaoProvider, sessionManagerProvider);
  }

  @Override
  public void injectMembers(GeofenceBroadcastReceiver instance) {
    injectAttendanceDao(instance, attendanceDaoProvider.get());
    injectSessionManager(instance, sessionManagerProvider.get());
  }

  @InjectedFieldSignature("com.autoexpert.app.service.GeofenceBroadcastReceiver.attendanceDao")
  public static void injectAttendanceDao(GeofenceBroadcastReceiver instance,
      AttendanceQueueDao attendanceDao) {
    instance.attendanceDao = attendanceDao;
  }

  @InjectedFieldSignature("com.autoexpert.app.service.GeofenceBroadcastReceiver.sessionManager")
  public static void injectSessionManager(GeofenceBroadcastReceiver instance,
      SessionManager sessionManager) {
    instance.sessionManager = sessionManager;
  }
}
