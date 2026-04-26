package com.autoexpert.app.service;

import com.autoexpert.app.data.local.dao.AttendanceQueueDao;
import com.autoexpert.app.util.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
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
public final class AttendanceGeofenceService_MembersInjector implements MembersInjector<AttendanceGeofenceService> {
  private final Provider<FusedLocationProviderClient> fusedLocationClientProvider;

  private final Provider<GeofencingClient> geofencingClientProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<AttendanceQueueDao> attendanceDaoProvider;

  public AttendanceGeofenceService_MembersInjector(
      Provider<FusedLocationProviderClient> fusedLocationClientProvider,
      Provider<GeofencingClient> geofencingClientProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<AttendanceQueueDao> attendanceDaoProvider) {
    this.fusedLocationClientProvider = fusedLocationClientProvider;
    this.geofencingClientProvider = geofencingClientProvider;
    this.sessionManagerProvider = sessionManagerProvider;
    this.attendanceDaoProvider = attendanceDaoProvider;
  }

  public static MembersInjector<AttendanceGeofenceService> create(
      Provider<FusedLocationProviderClient> fusedLocationClientProvider,
      Provider<GeofencingClient> geofencingClientProvider,
      Provider<SessionManager> sessionManagerProvider,
      Provider<AttendanceQueueDao> attendanceDaoProvider) {
    return new AttendanceGeofenceService_MembersInjector(fusedLocationClientProvider, geofencingClientProvider, sessionManagerProvider, attendanceDaoProvider);
  }

  @Override
  public void injectMembers(AttendanceGeofenceService instance) {
    injectFusedLocationClient(instance, fusedLocationClientProvider.get());
    injectGeofencingClient(instance, geofencingClientProvider.get());
    injectSessionManager(instance, sessionManagerProvider.get());
    injectAttendanceDao(instance, attendanceDaoProvider.get());
  }

  @InjectedFieldSignature("com.autoexpert.app.service.AttendanceGeofenceService.fusedLocationClient")
  public static void injectFusedLocationClient(AttendanceGeofenceService instance,
      FusedLocationProviderClient fusedLocationClient) {
    instance.fusedLocationClient = fusedLocationClient;
  }

  @InjectedFieldSignature("com.autoexpert.app.service.AttendanceGeofenceService.geofencingClient")
  public static void injectGeofencingClient(AttendanceGeofenceService instance,
      GeofencingClient geofencingClient) {
    instance.geofencingClient = geofencingClient;
  }

  @InjectedFieldSignature("com.autoexpert.app.service.AttendanceGeofenceService.sessionManager")
  public static void injectSessionManager(AttendanceGeofenceService instance,
      SessionManager sessionManager) {
    instance.sessionManager = sessionManager;
  }

  @InjectedFieldSignature("com.autoexpert.app.service.AttendanceGeofenceService.attendanceDao")
  public static void injectAttendanceDao(AttendanceGeofenceService instance,
      AttendanceQueueDao attendanceDao) {
    instance.attendanceDao = attendanceDao;
  }
}
