package com.autoexpert.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.autoexpert.app.MainActivity
import com.autoexpert.app.R
import com.autoexpert.app.data.local.dao.MessageDao
import com.autoexpert.app.data.local.entity.MessageEntity
import com.autoexpert.app.util.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class AutoExpertFirebaseService : FirebaseMessagingService() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var messageDao: MessageDao

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        scope.launch { sessionManager.saveFcmToken(token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val type = data["type"] ?: "general"

        scope.launch {
            when (type) {
                "message" -> handleIncomingMessage(data)
                "payout"  -> showSimpleNotification(
                    "💰 Payment Received",
                    data["body"] ?: "You have received a payout.",
                    CHANNEL_FINANCE, 3001
                )
                "leave_approved" -> showSimpleNotification(
                    "✅ Leave Approved",
                    data["body"] ?: "Your leave request has been approved.",
                    CHANNEL_LEAVES, 3002
                )
                "leave_rejected" -> showSimpleNotification(
                    "❌ Leave Rejected",
                    data["body"] ?: "Your leave request was not approved.",
                    CHANNEL_LEAVES, 3003
                )
                else -> {
                    val notif = message.notification ?: return@launch
                    showSimpleNotification(
                        notif.title ?: "Auto Expert",
                        notif.body ?: "",
                        CHANNEL_GENERAL, 3099
                    )
                }
            }
        }
    }

    private suspend fun handleIncomingMessage(data: Map<String, String>) {
        val baId = sessionManager.baId.first() ?: return
        val entity = MessageEntity(
            id          = data["message_id"] ?: UUID.randomUUID().toString(),
            senderId    = data["sender_id"] ?: "admin",
            senderName  = data["sender_name"] ?: "Admin",
            receiverId  = baId,
            body        = data["body"] ?: "",
            createdAt   = System.currentTimeMillis(),
            isRead      = false,
            isOutgoing  = false
        )
        messageDao.insert(entity)

        showSimpleNotification(
            "💬 ${entity.senderName}",
            entity.body,
            CHANNEL_MESSAGES,
            4000 + entity.id.hashCode() % 1000
        )
    }

    private fun showSimpleNotification(
        title: String, body: String, channelId: String, notifId: Int
    ) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannelIfNeeded(channelId)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("nav_to", channelId)
        }
        val pi = PendingIntent.getActivity(this, notifId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .build()

        nm.notify(notifId, notif)
    }

    private fun NotificationManager.createNotificationChannelIfNeeded(channelId: String) {
        val (name, importance) = when (channelId) {
            CHANNEL_MESSAGES -> "Messages" to NotificationManager.IMPORTANCE_HIGH
            CHANNEL_FINANCE  -> "Payments" to NotificationManager.IMPORTANCE_HIGH
            CHANNEL_LEAVES   -> "Leave Updates" to NotificationManager.IMPORTANCE_DEFAULT
            else             -> "General" to NotificationManager.IMPORTANCE_DEFAULT
        }
        if (getNotificationChannel(channelId) == null) {
            createNotificationChannel(NotificationChannel(channelId, name, importance))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val CHANNEL_MESSAGES = "messages"
        const val CHANNEL_FINANCE  = "finance"
        const val CHANNEL_LEAVES   = "leaves"
        const val CHANNEL_GENERAL  = "general"
    }
}
