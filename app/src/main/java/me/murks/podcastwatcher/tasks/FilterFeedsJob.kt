package me.murks.podcastwatcher.tasks

import android.app.job.JobParameters
import android.app.job.JobService
import me.murks.podcastwatcher.PodcastWatcherApp
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
        if(result.isNotEmpty()) {
            // TODO show notifications
        }
        jobFinished(parameter, false)
    }

}