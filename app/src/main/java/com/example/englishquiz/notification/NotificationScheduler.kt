package com.example.englishquiz.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class NotificationScheduler(
    private val context: Context,
) {
    companion object {
        const val NOTIFICATION_REQUEST_CODE = 101
    }

    /**
     * Schedules a daily notification at the specified time.
     *
     * @param hour Hour of the day (24-hour format).
     * @param minute Minute of the hour.
     */
    fun scheduleDailyNotification(
        hour: Int = 18,
        minute: Int = 0,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, NotificationReceiver::class.java)

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                NOTIFICATION_REQUEST_CODE,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        // Set the time for the notification
        val calendar =
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)

                // If the set time is before the current time, schedule it for the next day
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

        // Schedule the alarm
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent,
        )
    }

    /**
     * Cancels the scheduled notification.
     */
    fun cancelDailyNotification() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, NotificationReceiver::class.java)

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                NOTIFICATION_REQUEST_CODE,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        // Cancel the alarm
        alarmManager.cancel(pendingIntent)
    }
}
