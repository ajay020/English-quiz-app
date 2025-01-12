package com.example.englishquiz.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "daily_notification_channel"
        const val NOTIFICATION_ID = 100
    }

    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        NotificationHelper(context).sendNotification(
            CHANNEL_ID,
            "QuizzyBee Reminder",
            "It's time to practice today's quiz!",
            NOTIFICATION_ID,
        )
    }
}
