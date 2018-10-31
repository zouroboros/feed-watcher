package me.murks.feedwatcher.model

import java.net.URL

/**
 * Base class for filters
 * @author zouroboros
 */
abstract class Filter(val type: FilterType, val index: Int) {

    abstract fun filterItems(feed: Feed, items: List<FeedItem>): List<FeedItem>

    abstract fun <R>filterCallback(callback: FilterTypeCallback<R>): R

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Filter

        if (type != other.type) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + index
        return result
    }
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContainsFilter

        if (text != other.text) return false

        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + text.hashCode()
        return result
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as FeedFilter

        if (feedUrl != other.feedUrl) return false

        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + feedUrl.hashCode()
        return result
    }

}