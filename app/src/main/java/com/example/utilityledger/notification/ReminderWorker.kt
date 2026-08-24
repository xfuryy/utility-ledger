package com.example.utilityledger.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.utilityledger.data.AppDatabase
import com.example.utilityledger.repository.LedgerRepository
import java.time.LocalDate

/**
 * Runs roughly once a day. Anything unpaid with a due date within the next
 * 2 days gets a notification. Each item's notification id is derived from its
 * row id so the same bill won't spam duplicate notifications across days.
 */
class ReminderWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val repo = LedgerRepository(db.entryDao(), db.portalDao())
        val horizon = LocalDate.now().plusDays(2)

        val dueEntries = repo.unpaidEntriesDueBy(horizon)
        val duePortals = repo.unpaidPortalsDueBy(horizon)

        if (dueEntries.isEmpty() && duePortals.isEmpty()) return Result.success()

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted (or not yet requested) — nothing we can post.
            return Result.success()
        }

        val manager = NotificationManagerCompat.from(applicationContext)

        dueEntries.forEach { entry ->
            val title = entry.category + (if (entry.name.isNotBlank()) " - ${entry.name}" else "") + " due soon"
            val text = "Due ${entry.dueDate}" + (if (entry.amount > 0) " · ${entry.amount}" else "")
            val notification = NotificationCompat.Builder(applicationContext, REMINDER_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .build()
            manager.notify(("entry_" + entry.id).hashCode(), notification)
        }

        duePortals.forEach { portal ->
            val title = "${portal.name} recharge due soon"
            val text = "Due ${portal.dueDate} · balance ${portal.currentBalance} (min ${portal.minBalance})"
            val notification = NotificationCompat.Builder(applicationContext, REMINDER_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .build()
            manager.notify(("portal_" + portal.id).hashCode(), notification)
        }

        return Result.success()
    }
}
