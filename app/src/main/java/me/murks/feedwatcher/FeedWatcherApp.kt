package me.murks.feedwatcher

import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.model.Query
import me.murks.feedwatcher.model.Result
import me.murks.feedwatcher.tasks.ActionTask
import me.murks.feedwatcher.tasks.ErrorHandlingTaskListener
import java.io.Closeable
import java.lang.Exception
import java.util.*

/**
 * @author zouroboros
 * @date 8/13/18.
 */
class FeedWatcherApp(private val environment: Environment): Closeable {

    fun queries(): List<Query> {
        val queries = environment.dataStore.getQueries()
        return queries
    }

    fun feeds(): List<Feed> {
        val feeds = environment.dataStore.getFeeds()
        return feeds
    }


    fun results(listener: ErrorHandlingTaskListener<List<Result>, List<Result>, Exception>): ActionTask<List<Result>>
            = ActionTask({ environment.dataStore.getResults()}, listener)

    fun updateQuery(query: Query) {
        environment.dataStore.updateQuery(query)
    }

    fun addQuery(query: Query) {
        environment.dataStore.addQuery(query)
    }

    fun addFeed(feed: Feed) {
        environment.dataStore.addFeed(feed)
    }

    fun updateFeed(feed: Feed) {
        environment.dataStore.updateFeed(feed)
    }

    fun addResult(result: Result) {
        val newFeed = Feed(result.feed.url, Date(), result.feed.name)
        environment.dataStore.addResultAndUpdateFeed(result, newFeed)
    }

    fun delete(feed: Feed) {
        environment.dataStore.delete(feed)
    }

    fun query(id: Long): Query {
        return environment.dataStore.query(id)
    }

    override fun close() {
        environment.dataStore.close()
    }

    fun result(id: Long): Result {
        return environment.dataStore.result(id)
    }

    fun delete(result: Result) {
        environment.dataStore.delete(result)
    }

    fun delete(query: Query) {
        environment.dataStore.delete(query)
    }

    /**
     * Reschedules the background scanning according to the current settings
     */
    fun rescheduleJobs() {
        environment.jobs.rescheduleJobs(environment.settings)
    }

    fun showNotifications(results: List<Result>) {
        if(results.isNotEmpty()) {
            environment.notifications.newResults(results, environment.settings)
        }
    }
}