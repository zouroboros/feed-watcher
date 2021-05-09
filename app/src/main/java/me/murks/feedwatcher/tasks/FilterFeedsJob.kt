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
Copyright 2019-2020 Zouroboros
 */
package me.murks.feedwatcher.tasks

import android.app.job.JobParameters
import android.app.job.JobService
import me.murks.feedwatcher.AndroidEnvironment
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.Right
import me.murks.feedwatcher.model.Result
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.Exception

/**
 * @author zouroboros
 */
class FilterFeedsJob(): JobService() {

    private lateinit var app: FeedWatcherApp
    private lateinit var parameter: JobParameters
    private lateinit var future: CompletableFuture<Unit>

    override fun onStartJob(p0: JobParameters): Boolean {
        parameter = p0
        app = FeedWatcherApp(AndroidEnvironment(this))
        app.environment.log.info("Starting ${FilterFeedsJob::class.qualifiedName}.")
        future = Tasks.filterFeeds(app)
        app.environment.log.info("${FilterFeedsJob::class.qualifiedName} started.")
        return true // job may still be running
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        app.environment.log.info("${FilterFeedsJob::class.qualifiedName} canceled.")
        future.cancel(true)
        return false // no rescheduling
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::app.isInitialized) {
            app.environment.close()
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
    }
}