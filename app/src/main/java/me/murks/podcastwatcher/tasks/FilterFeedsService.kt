package me.murks.podcastwatcher.tasks

import android.app.IntentService
import android.content.Intent
import me.murks.podcastwatcher.PodcastWatcherApp
import me.murks.podcastwatcher.io.items
import me.murks.podcastwatcher.io.loadFeedUiContainer
import me.murks.podcastwatcher.model.Feed
import me.murks.podcastwatcher.model.Result
import java.util.*

class FilterFeedsService : IntentService("FilterFeedsService") {

    override fun onHandleIntent(intent: Intent?) {
        val app = PodcastWatcherApp()
        val feeds = app.feeds
        val queries = app.queries

        for (feed in feeds) {
            val feedName = loadFeedUiContainer(feed).name
            for (query in queries){
                val items = items(feed.url, feed.lastUpdate)
                val found = query.filter.fold(items) { acc, filter -> filter.filterItems(feed, feedName, acc) }
                if (found.isNotEmpty()) {
                    found.map { Result(feed, query, it, Date(), feedName) }
                }
                app.updateFeed(Feed(feed.url, Date()))
            }
        }
        // TODO add notifications
        // TODO add scheduling
    }


}
