package me.murks.feedwatcher.data

import me.murks.feedwatcher.model.Feed

/**
 * Unit of Work for adding feeds
 * @author zouroboros
 */
class AddFeeds(private val feeds: Collection<Feed>): UnitOfWork {
    override fun execute(store: DataStore) {
        store.startTransaction()
        feeds.forEach { feed ->
            store.addFeed(feed)
        }
        store.commitTransaction()
    }
}