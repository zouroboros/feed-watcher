package me.murks.feedwatcher.model

import java.net.URL

/**
 * Base class for filters
 * @author zouroboros
 */
abstract class Filter(val type: FilterType, val index: Int) {

    abstract fun filterItems(feed: Feed, items: List<FeedItem>): List<FeedItem>

    abstract fun <R>filterCallback(callback: FilterTypeCallback<R>): R
}

interface FilterTypeCallback<R> {
    fun filter(filter: ContainsFilter): R
    fun filter(filter: FeedFilter): R
}

class ContainsFilter(index: Int, val text: String): Filter(FilterType.CONTAINS, index) {

    override fun filterItems(feed: Feed, items: List<FeedItem>): List<FeedItem> {
        return items.filter { it.title.contains(text, true)
                || it.description.contains(text, true) }
    }

    override fun <R> filterCallback(callback: FilterTypeCallback<R>): R {
        return callback.filter(this)
    }
}

class FeedFilter(index: Int, val feedUrl: URL): Filter(FilterType.FEED, index) {
    override fun filterItems(feed: Feed, items: List<FeedItem>): List<FeedItem> {
        return if(feed.url == feedUrl) {
            items
        } else {
            emptyList()
        }
    }

    override fun <R> filterCallback(callback: FilterTypeCallback<R>): R {
        return callback.filter(this)
    }

}