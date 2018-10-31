package me.murks.feedwatcher.activities

import me.murks.feedwatcher.model.ContainsFilter
import me.murks.feedwatcher.model.Filter
import me.murks.feedwatcher.model.FilterType
import me.murks.feedwatcher.model.FilterTypeCallback
import java.lang.IllegalStateException

/**
 * @author zouroboros
 */
class FilterUiModel(var type: FilterType): FilterTypeCallback<Unit> {
    lateinit var containsText: String

    constructor(filter: Filter): this(filter.type) {
        filter.filterCallback(this)
    }

    override fun filter(filter: ContainsFilter) {
        containsText = filter.text
    }

    fun filter(index: Int): Filter {
        if(type == FilterType.CONTAINS) {
            return ContainsFilter(index, containsText)
        }
        throw IllegalStateException()
    }
}