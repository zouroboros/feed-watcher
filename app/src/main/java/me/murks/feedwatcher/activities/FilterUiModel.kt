package me.murks.feedwatcher.activities

import me.murks.feedwatcher.model.*
import java.lang.IllegalStateException

/**
 * @author zouroboros
 */
class FilterUiModel(var type: FilterType, val feeds: List<Feed>): FilterTypeCallback<Unit> {
    var containsText: String = ""
    var selectedType = FilterType.values().indexOf(type)
    var selectedFeed = -1
    val feedNames = feeds.map { it.name }

    constructor(filter: Filter, feeds: List<Feed>): this(filter.type, feeds) {
        filter.filterCallback(this)
    }

    override fun filter(filter: ContainsFilter) {
        containsText = filter.text
    }

    fun filter(index: Int): Filter {
        if(type == FilterType.CONTAINS) {
            return ContainsFilter(index, containsText)
        } else if(type == FilterType.FEED) {
            return FeedFilter(index, feeds[selectedFeed].url)
        }
        throw IllegalStateException()
    }

    override fun filter(filter: FeedFilter) {
        selectedFeed = feeds.map { it.url }.indexOf(filter.feedUrl)
    }
}