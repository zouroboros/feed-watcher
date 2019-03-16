package me.murks.feedwatcher

import android.app.Application
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build


/**
 * @author zouroboros
 */
class AndroidApplication(): Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val app = FeedWatcherApp(AndroidEnvironment(this))
        // TODO only schedule job when at least one query is set up
        app.rescheduleJobs()

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.result_notification_channel_name)
            val description = getString(R.string.result_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            channel.importance = NotificationManager.IMPORTANCE_LOW
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "result.notification.channel"
    }
}