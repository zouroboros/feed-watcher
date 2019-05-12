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
package me.murks.feedwatcher.model

import java.net.URL

/**
 * Base class for filters
 * @author zouroboros
 */
abstract class Filter(val type: FilterType, val index: Int) {

    abstract fun filterItems(feed: Feed, items: List<FeedItem>): List<FeedItem>

    abstract fun <R>filterCallback(callback: FilterTypeCallback<R>): R

    /**
     * Returns the parameter of a filter
     */
    abstract fun parameter(): List<FilterParameter>

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