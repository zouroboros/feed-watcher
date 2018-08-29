package me.murks.podcastwatcher

import me.murks.podcastwatcher.data.DataStore
import me.murks.podcastwatcher.model.Feed
import me.murks.podcastwatcher.model.Query
import me.murks.podcastwatcher.model.Result

/**
 * @author zouroboros
 * @date 8/13/18.
 */
class PodcastWatcherApp() {

    private val dataStore = DataStore()

    val queries: List<Query>
        get() = dataStore.getQueries()

    val feeds: List<Feed>
        get() = dataStore.getFeeds()

    val results: List<Result>
        get() = dataStore.getResults()

    fun updateQuery(query: Query) {
        dataStore.updateQuery(query)
    }

    fun addQuery(query: Query) {
        dataStore.addQuery(query)
    }

    fun addFeed(feed: Feed) {
        dataStore.addFeed(feed)
    }

    fun updateFeed(feed: Feed) {
        dataStore.updateFeed(feed)
    }

}