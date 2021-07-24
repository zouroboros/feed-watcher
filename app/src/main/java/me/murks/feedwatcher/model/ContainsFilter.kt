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

class ContainsFilter(index: Int, val text: String?): Filter(FilterType.CONTAINS, index) {

    companion object {
        const val textParameterName = "text"
    }

    override fun filterItems(feed: Feed, items: List<FeedItem>): List<FeedItem> {
        return items.filter { it.title.contains(text ?: "", true)
                || it.description.contains(text ?: "", true) }
    }

    override fun <R> filterCallback(callback: FilterTypeCallback<R>): R {
        return callback.filter(this)
    }

    override fun parameter(): List<FilterParameter> {
        return listOf(FilterParameter(textParameterName, text, null))
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