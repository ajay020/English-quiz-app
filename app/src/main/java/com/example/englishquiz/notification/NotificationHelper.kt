package com.example.englishquiz.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.englishquiz.R
import com.example.englishquiz.ui.MainActivity

class NotificationHelper(
    private val context: Context,
) {
    companion object {
        const val DAILY_NOTIFICATION_CHANNEL_ID = "daily_notification_channel"
        const val DAILY_NOTIFICATION_CHANNEL_NAME = "Daily Notifications"
        const val DAILY_NOTIFICATION_DESCRIPTION = "Reminders to practice daily quizzes."
    }

    init {
        createNotificationChannels()
    }

    // Create notification channels
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dailyChannel =
                NotificationChannel(
                    DAILY_NOTIFICATION_CHANNEL_ID,
                    DAILY_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = DAILY_NOTIFICATION_DESCRIPTION
                }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(dailyChannel)
        }
    }

    // Send a notification
    fun sendNotification(
        channelId: String,
        title: String,
        message: String,
        notificationId: Int,
    ) {
        // Create an intent to launch MainActivity
        val intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        // Create a pending intent to wrap the intent
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0, // Request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat
                .Builder(context, channelId)
                .setSmallIcon(R.drawable.bee_play) // Replace with your app's icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent) // Set the PendingIntent
                .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.notify(notificationId, notification)
    }
}
