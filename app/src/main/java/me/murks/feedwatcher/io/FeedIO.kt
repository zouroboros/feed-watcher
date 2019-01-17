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
import java.net.URL
import java.util.*

/**
 * Class for loading data from rss or atom feeds
 * @author zouroboros
 */
class FeedIO(url: URL) {

    private val source = XmlReader(url.finalUrl()).use {
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
        val date = entry.publishedDate
        return FeedItem(title, description, link, date)
    }
}