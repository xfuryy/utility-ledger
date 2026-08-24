package com.example.utilityledger.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

const val REMINDER_CHANNEL_ID = "bill_reminders"
private const val REMINDER_WORK_NAME = "daily_reminder_check"

object NotificationScheduler {

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Bill and recharge reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts you 2 days before a bill or portal recharge is due"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /** Schedules a daily background check. Safe to call every app start — WORK is kept if already scheduled. */
    fun scheduleDailyCheck(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
