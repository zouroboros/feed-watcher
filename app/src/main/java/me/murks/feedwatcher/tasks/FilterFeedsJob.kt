package me.murks.feedwatcher.tasks

import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.murks.feedwatcher.AndroidApplication
import me.murks.feedwatcher.AndroidEnvironment
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
        app = FeedWatcherApp(AndroidEnvironment(this))
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
        error.printStackTrace()
        jobFinished(parameter, false)
    }

    override fun onSuccessResult(results: List<Result>) {
        app.showNotifications(results)

        jobFinished(parameter, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::app.isInitialized) {
            app.close()
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
    }
}