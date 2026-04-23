package com.autoexpert.app.service

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.autoexpert.app.R
import com.autoexpert.app.data.local.dao.AttendanceQueueDao
import com.autoexpert.app.data.local.entity.AttendanceQueueEntity
import com.autoexpert.app.util.SessionManager
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceGeofenceService : Service() {

    @Inject lateinit var fusedLocationClient: FusedLocationProviderClient
    @Inject lateinit var geofencingClient: GeofencingClient
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var attendanceDao: AttendanceQueueDao

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIF_ID, buildForegroundNotification())
        scope.launch { setupGeofence() }
    }

    private suspend fun setupGeofence() {
        val lat    = sessionManager.stationLat.first() ?: return
        val lng    = sessionManager.stationLng.first() ?: return
        val radius = sessionManager.stationRadius.first().toFloat()
        val stId   = sessionManager.stationId.first() ?: return

        if (!ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                .let { it == PackageManager.PERMISSION_GRANTED }) return

        val geofence = Geofence.Builder()
            .setRequestId(stId)
            .setCircularRegion(lat, lng, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setLoiteringDelay(60_000) // 1 min dwell
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        geofencingClient.addGeofences(request, intent)
    }

    private fun buildForegroundNotification(): Notification {
        val channelId = "geofence_service"
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(channelId, "Attendance Tracking", NotificationManager.IMPORTANCE_LOW)
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Auto Expert")
            .setContentText("Tracking your station arrival for attendance")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val NOTIF_ID = 1001
        const val CHANNEL_ATTENDANCE = "attendance"
    }
}

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var attendanceDao: AttendanceQueueDao
    @Inject lateinit var sessionManager: SessionManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            Log.e("Geofence", "Error: ${event.errorCode}")
            return
        }

        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            scope.launch {
                markAttendanceByGps(context, event)
            }
        }
    }

    private suspend fun markAttendanceByGps(context: Context, event: GeofencingEvent) {
        val baId = sessionManager.baId.first() ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Check not already marked today
        val existing = attendanceDao.getByBaAndDate(baId, today)
        if (existing != null) return

        val location = event.triggeringLocation
        val entity = AttendanceQueueEntity(
            localId        = UUID.randomUUID().toString(),
            baId           = baId,
            attendanceDate = today,
            status         = "P",
            markedByGps    = true,
            latitude       = location?.latitude,
            longitude      = location?.longitude,
            syncStatus     = "pending"
        )
        attendanceDao.insert(entity)

        // Show notification
        showAttendanceNotification(context)

        // Trigger sync immediately
        SyncWorker.triggerImmediateSync(context)
    }

    private fun showAttendanceNotification(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java)
        val channelId = "attendance_marked"
        nm.createNotificationChannel(
            NotificationChannel(channelId, "Attendance", NotificationManager.IMPORTANCE_DEFAULT)
        )
        val notif = NotificationCompat.Builder(context, channelId)
            .setContentTitle("✅ Attendance Marked")
            .setContentText("You've arrived at your station. Attendance recorded automatically.")
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .build()
        nm.notify(2001, notif)
    }
}
