package me.murks.feedwatcher

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.murks.feedwatcher.activities.OverviewActivity
import me.murks.feedwatcher.tasks.FilterFeedsJob
import me.murks.feedwatcher.model.Result

/**
 * Class for managing notifications
 * @author zouroboros
 */
class Notifications(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "result.notification.channel"
    }

    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.result_notification_channel_name)
            val description = context.getString(R.string.result_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            channel.importance = NotificationManager.IMPORTANCE_LOW
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    fun newResults(results: List<Result>, settings: Settings) {
        if(settings.showNotifcations) {
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            notificationBuilder.setSmallIcon(R.drawable.ic_feedwatcher_notification)
            notificationBuilder.setContentTitle(context.getString(R.string.result_notification_title))

            val feeds = results.map { it.feed.name }.joinToString(", ")

            notificationBuilder.setContentText(
                    String.format(context.getString(R.string.result_notification_content), feeds))

            notificationBuilder.setAutoCancel(true)

            val intent = Intent(context, OverviewActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(OverviewActivity.CURRENT_FRAGMENT, R.id.nav_results)

            notificationBuilder.setContentIntent(
                    PendingIntent.getActivity(context, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT))

            notificationBuilder.setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(FilterFeedsJob.NOTIFICATION_ID, notificationBuilder.build())
        }
    }
}