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
along with FeedWatcher. If not, see <https://www.gnu.org/licenses/>.
Copyright 2021 Zouroboros
 */
package me.murks.feedwatcher

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import me.murks.feedwatcher.model.ScanInterval
import me.murks.feedwatcher.tasks.FilterFeedsJob
import java.lang.RuntimeException
import kotlin.math.max

/**
 * Class for managing background jobs
 * @author zouroboros
 */
class Jobs(private val context: Context) {

    fun rescheduleJobs(scanInterval: ScanInterval) {
        val jobScheduler = context.getSystemService(JobScheduler::class.java)

        jobScheduler.cancelAll()

        if(jobScheduler.allPendingJobs.isEmpty()) {
            val jobBuilder = JobInfo.Builder(1, ComponentName(context, FilterFeedsJob::class.java))

            val interval = scanInterval.hours * 60 * 60 * 1000L + scanInterval.minutes * 60 * 1000L

            jobBuilder.setPeriodic(interval, max(JobInfo.getMinFlexMillis(), interval/10))
                    .setPersisted(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            val result = jobScheduler.schedule(jobBuilder.build())
            if(result != JobScheduler.RESULT_SUCCESS) {
                throw RuntimeException("Couldn't schedule job!")
            }
            // TODO only schedule job when at least one query is set up
        }
    }

    fun removeSchedule() {
        val jobScheduler = context.getSystemService(JobScheduler::class.java)
        jobScheduler.cancelAll()
    }
}