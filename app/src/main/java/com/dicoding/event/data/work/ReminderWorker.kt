package com.dicoding.event.data.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dicoding.event.R
import com.dicoding.event.data.retrofit.ApiConfig
import com.dicoding.event.ui.detail.DetailActivity

class ReminderWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding_event_channel"
    }

    override suspend fun doWork(): Result {
        return try {
            val apiService = ApiConfig.getApiService()
            // Fetch the nearest active event (active = -1 for search/all, limit = 1)
            // Or use active = 1 for upcoming. Let's use active = 1 as per requirement "event aktif terdekat"
            val response = apiService.getEvents(active = 1)
            val event = response.listEvents.firstOrNull()

            if (event != null) {
                showNotification(event.name, event.beginTime, event.id.toString())
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error fetching event: ${e.message}")
            Result.failure()
        }
    }

    private fun showNotification(title: String, time: String, eventId: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, eventId)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_upcoming) // Reuse existing icon
            .setContentTitle("Event Reminder: $title")
            .setContentText("Event starts at $time")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
