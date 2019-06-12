/*
This file is part of FeedWatcher.

FeedWatcher is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FeedWatcher is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FeedWatcher.  If not, see <https://www.gnu.org/licenses/>.
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.activities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import me.murks.feedwatcher.model.*
import java.lang.IllegalStateException
import java.util.*

/**
 * @author zouroboros
 */
class FilterUiModel(var type: FilterType, val feeds: List<Feed>): FilterTypeCallback<Unit> {
    var containsText: String = ""
    var selectedType: Int
        get() = FilterType.values().indexOf(type)
        set(value) { type = FilterType.values()[value] }
    var selectedFeed: Int? = null
    var startDate: Date = Date()
    val feedNames = feeds.map { it.name }

    constructor(filter: Filter, feeds: List<Feed>): this(filter.type, feeds) {
        filter.filterCallback(this)
    }

    fun filter(index: Int): Filter {
        if(type == FilterType.CONTAINS) {
            return ContainsFilter(index, containsText)
        } else if(type == FilterType.FEED) {
            return FeedFilter(index, if (selectedFeed != null) feeds[selectedFeed!!].url else null)
        } else if(type == FilterType.NEW) {
            return NewEntryFilter(index, startDate)
        }
        throw IllegalStateException()
    }

    override fun filter(filter: FeedFilter) {
        selectedFeed = feeds.map { it.url }.indexOf(filter.feedUrl)
    }

    override fun filter(filter: ContainsFilter) {
        containsText = filter.text?: ""
    }

    override fun filter(filter: NewEntryFilter) {
        startDate = filter.start
    }


}