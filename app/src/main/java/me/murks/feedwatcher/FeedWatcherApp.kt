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
class FeedWatcherApp(private val env: Environment): Closeable {

    fun queries(): List<Query> {
        val queries = env.dataStore.getQueries()
        return queries
    }

    fun feeds(): List<Feed> {
        val feeds = env.dataStore.getFeeds()
        return feeds
    }


    fun results(listener: ErrorHandlingTaskListener<List<Result>, List<Result>, Exception>): ActionTask<List<Result>>
            = ActionTask({ env.dataStore.getResults()}, listener)

    fun updateQuery(query: Query) {
        env.dataStore.updateQuery(query)
    }

    fun addQuery(query: Query) {
        env.dataStore.addQuery(query)
    }

    fun addFeed(feed: Feed) {
        env.dataStore.addFeed(feed)
    }

    fun updateFeed(feed: Feed) {
        env.dataStore.updateFeed(feed)
    }

    fun addResult(result: Result) {
        val newFeed = Feed(result.feed.url, Date(), result.feed.name)
        env.dataStore.addResultAndUpdateFeed(result, newFeed)
    }

    fun delete(feed: Feed) {
        env.dataStore.delete(feed)
    }

    fun query(id: Long): Query {
        return env.dataStore.query(id)
    }

    override fun close() {
        env.dataStore.close()
    }

    fun result(id: Long): Result {
        return env.dataStore.result(id)
    }

    fun delete(result: Result) {
        env.dataStore.delete(result)
    }

    fun delete(query: Query) {
        env.dataStore.delete(query)
    }

    /**
     * Reschedules the background scanning to the current settings
     */
    fun rescheduleScanner() {

    }
}