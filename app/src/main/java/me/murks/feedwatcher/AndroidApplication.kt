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

        Notifications(this).createNotificationChannel()

        val app = FeedWatcherApp(AndroidEnvironment(this))
        // TODO only schedule job when at least one query is set up
        app.rescheduleJobs()

    }
}