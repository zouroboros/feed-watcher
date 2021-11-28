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
Copyright 2019 - 2021 Zouroboros
 */
package me.murks.feedwatcher.model

import me.murks.feedwatcher.atomrss.FeedItem
import java.util.*

/**
 * Filter for new entries in a feed. Filters all entries after a certain date
 * @author zouroboros
 */
class NewEntryFilter(index: Int, val start: Date): Filter(FilterType.NEW, index) {

    companion object {
        const val startDateParameterName = "startDate"
    }

    override fun <R> filterCallback(callback: FilterTypeCallback<R>): R {
        return callback.filter(this)
    }

    override fun filterItems(feed: Feed, items: List<FeedItem>): List<FeedItem> {
        return items.filter { it.date.after(start) }
    }

    override fun parameter(): List<FilterParameter> {
        return listOf(FilterParameter(startDateParameterName, null, start))
    }
}