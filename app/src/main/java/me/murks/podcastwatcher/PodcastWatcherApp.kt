package me.murks.podcastwatcher

import me.murks.podcastwatcher.data.DataStore
import me.murks.podcastwatcher.model.Feed
import me.murks.podcastwatcher.model.Query

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

}