package me.murks.podcastwatcher.tasks

import android.app.job.JobParameters
import android.app.job.JobService
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import me.murks.podcastwatcher.AndroidApplication
import me.murks.podcastwatcher.PodcastWatcherApp
import me.murks.podcastwatcher.R
import me.murks.podcastwatcher.model.Result
import kotlin.Exception

/**
 * @author zouroboros
 */
class FilterFeedsJob(): JobService(), ErrorHandlingTaskListener<Result, List<Result>, Exception> {

    private val app = PodcastWatcherApp()
    private val task = FilterFeedsTask(app, ErrorHandlingTaskListenerWrapper(this))
    private var parameter: JobParameters? = null

    override fun onStartJob(p0: JobParameters?): Boolean {
        parameter = p0
        task.execute(*app.feeds.toTypedArray())
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
        println(result.isEmpty())
        if(result.isNotEmpty()) {
            val notificationBuilder = NotificationCompat.Builder(this, AndroidApplication.CHANNEL_ID)
            notificationBuilder.setSmallIcon(R.drawable.notification_icon_background)
            notificationBuilder.setContentTitle(getString(R.string.result_notification_title))

            val feeds = result.map { it.feedName }.joinToString(", ")

            notificationBuilder.setContentText(
                    String.format(getString(R.string.result_notification_content), feeds))

            notificationBuilder.setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
        jobFinished(parameter, false)
    }

    companion object {
        const val NOTIFICATION_ID = 1
    }
}