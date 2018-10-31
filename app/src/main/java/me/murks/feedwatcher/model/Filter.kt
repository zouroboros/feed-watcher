package me.murks.feedwatcher.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author zouroboros
 * @date 8/13/18.
 */
abstract class Filter(val type: FilterType, val index: Int) {

    abstract fun filterItems(feed: Feed, feedName: String, items: List<FeedItem>): List<FeedItem>

    abstract fun <R>filterCallback(callback: FilterTypeCallback<R>): R
}

interface FilterTypeCallback<R> {
    fun filter(filter: ContainsFilter): R
}

class ContainsFilter(index: Int, val text: String): Filter(FilterType.CONTAINS, index) {

    override fun filterItems(feed: Feed, feedName: String, items: List<FeedItem>): List<FeedItem> {
        return items.filter { it.title.contains(text, true)
                || it.description.contains(text, true) }
    }

    override fun <R> filterCallback(callback: FilterTypeCallback<R>): R {
        return callback.filter(this)
    }

}