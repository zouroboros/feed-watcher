package me.murks.feedwatcher.tasks

import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import me.murks.feedwatcher.AndroidApplication
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.R
import me.murks.feedwatcher.activities.OverviewActivity
import me.murks.feedwatcher.model.Result
import kotlin.Exception

/**
 * @author zouroboros
 */
class FilterFeedsJob(): JobService(), ErrorHandlingTaskListener<Result, List<Result>, Exception> {

    private lateinit var app: FeedWatcherApp
    private lateinit var task: FilterFeedsTask
    private lateinit var parameter: JobParameters

    override fun onStartJob(p0: JobParameters): Boolean {
        parameter = p0
        app = FeedWatcherApp(this)
        task =  FilterFeedsTask(app, ErrorHandlingTaskListenerWrapper(this))
        task.execute(*app.feeds().toTypedArray())
        return true // job may still be running
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        task.cancel(true)
        return false // no rescheduling
    }

    override fun onProgress(progress: Result) {
    }

    override fun onErrorResult(error: Exception) {
        jobFinished(parameter, false)
    }

    override fun onSuccessResult(result: List<Result>) {
        if(result.isNotEmpty()) {
            val notificationBuilder = NotificationCompat.Builder(this, AndroidApplication.CHANNEL_ID)
            notificationBuilder.setSmallIcon(R.drawable.notification_icon_background)
            notificationBuilder.setContentTitle(getString(R.string.result_notification_title))

            val feeds = result.map { it.feedName }.joinToString(", ")

            notificationBuilder.setContentText(
                    String.format(getString(R.string.result_notification_content), feeds))

            notificationBuilder.setAutoCancel(true)

            val intent = Intent(this, OverviewActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(OverviewActivity.CURRENT_FRAGMENT, R.id.nav_results)

            notificationBuilder.setContentIntent(
                    PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT))

            notificationBuilder.setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            // TODO click on notification should open results fragment
        }

        jobFinished(parameter, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        app.close()
    }

    companion object {
        const val NOTIFICATION_ID = 1
    }
}