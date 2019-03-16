package me.murks.feedwatcher

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import me.murks.feedwatcher.tasks.FilterFeedsJob
import java.lang.RuntimeException

/**
 * Class for managing background jobs
 * @author zouroboros
 */
class Jobs(private val context: Context) {

    fun rescheduleJobs(settings: Settings) {
        val jobScheduler = context.getSystemService(JobScheduler::class.java)

        jobScheduler.cancelAll()

        if(settings.backgroundScanning) {
            if(jobScheduler.allPendingJobs.isEmpty()) {
                val jobBuilder = JobInfo.Builder(1, ComponentName(context, FilterFeedsJob::class.java))


                jobBuilder.setPeriodic(settings.backgroundScanInterval * 60 * 60 * 1000L)
                        .setPersisted(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                val result = jobScheduler.schedule(jobBuilder.build())
                if(result != JobScheduler.RESULT_SUCCESS) {
                    throw RuntimeException("Couldn't schedule job!")
                }
                // TODO only schedule job when at least one query is set up
            }
        }
    }
}