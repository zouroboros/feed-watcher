/*
This file is part of FeedWatcher.

FeedWatcher is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FeedWatcher is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FeedWatcher.  If not, see <https://www.gnu.org/licenses/>.
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.tasks

import android.app.job.JobParameters
import android.app.job.JobService
import me.murks.feedwatcher.AndroidEnvironment
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.model.Result
import java.util.*
import kotlin.Exception

/**
 * @author zouroboros
 */
class FilterFeedsJob(): JobService(), ErrorHandlingTaskListener<Result, List<Result>, Exception> {

    private lateinit var app: FeedWatcherApp
    private lateinit var task: FilterFeedsTask
    private lateinit var parameter: JobParameters
    private lateinit var results: MutableList<Result>

    override fun onStartJob(p0: JobParameters): Boolean {
        results = LinkedList();
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

    override fun onProgress(result: Result) {
        results.add(result)
    }

    override fun onErrorResult(error: Exception) {
        error.printStackTrace()
        jobFinished(parameter, false)
    }

    override fun onSuccessResult(results: List<Result>) {
        app.scanResults(results);
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