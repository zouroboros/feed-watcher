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
 * @author zouroboros
 */
class FeedFilter(index: Int, val feedUrl: URL?): Filter(FilterType.FEED, index) {
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