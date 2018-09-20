package me.murks.feedwatcher

import android.content.Context
import me.murks.feedwatcher.data.DataStore
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.model.Query
import me.murks.feedwatcher.model.Result
import java.util.*

/**
 * @author zouroboros
 * @date 8/13/18.
 */
class FeedWatcherApp(private val context: Context) {

    fun queries(): List<Query> {
            val dataStore = DataStore(context)
            val queries = dataStore.getQueries()
            dataStore.close()
            return queries
        }

    fun feeds(): List<Feed> {
        val dataStore = DataStore(context)
        val feeds = dataStore.getFeeds()
        dataStore.close()
        return feeds
    }


    fun results(): List<Result> {
        val dataStore = DataStore(context)
        val results = dataStore.getResults()
        dataStore.close()
        return results
    }

    fun updateQuery(query: Query) {
        val dataStore = DataStore(context)
        dataStore.updateQuery(query)
        dataStore.close()
    }

    fun addQuery(query: Query) {
        val dataStore = DataStore(context)
        dataStore.addQuery(query)
        dataStore.close()
    }

    fun addFeed(feed: Feed) {
        val dataStore = DataStore(context)
        dataStore.addFeed(feed)
        dataStore.close()
    }

    fun updateFeed(feed: Feed) {
        val dataStore = DataStore(context)
        dataStore.updateFeed(feed)
        dataStore.close()
    }

    fun addResult(result: Result) {
        val dataStore = DataStore(context)
        dataStore.addResultAndUpdateFeed(result, Feed(result.feed.url, Date()))
        dataStore.close()
    }
}