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
package me.murks.feedwatcher.io

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import me.murks.feedwatcher.activities.FeedUiContainer
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.model.FeedItem
import java.io.InputStream
import java.net.URL
import java.util.*

/**
 * Class for loading data from rss or atom feeds
 * @author zouroboros
 */
class FeedIO(inputStream: InputStream) {

    private val source = XmlReader(inputStream).use {
        SyndFeedInput().build(it)
    }

    fun feedUiContainer(url: URL, name: String? = null, updated: Date? = null): FeedUiContainer {
        val author = source.author ?: itunesAuthor(source) ?: source.generator ?: url.toString()
        var icon: Bitmap? = null
        val iconUrl = source.icon?.url ?: source.image?.url
        if (iconUrl != null) {
            URL(iconUrl).finalUrl().openStream().use {
                icon = BitmapFactory.decodeStream(it)
            }
        }
        val description = source.description
        return FeedUiContainer(name?: source.title, author, icon, description, url, updated)
    }

    fun feedUiContainer(feed: Feed) = feedUiContainer(feed.url, feed.name, feed.lastUpdate)

    private fun itunesAuthor(syndFeed: SyndFeed): String? {
        return syndFeed.foreignMarkup.filter { it.name == "author" }.map { it.value }.lastOrNull()
    }

    fun items(since: Date): List<FeedItem> {
        return source.entries.map(::item2FeedItem).filter { it.date.after(since) }
    }

    private fun item2FeedItem(entry: SyndEntry): FeedItem {
        val title = entry.title
        val description = entry.description.value
        val link = if (entry.link != null) URL(entry.link) else null
        val date = entry.publishedDate ?: entry.updatedDate ?: Date()
        return FeedItem(title, description, link, date)
    }
}