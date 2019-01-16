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
 * @author zouroboros
 * @date 8/15/18.
 */

fun loadFeedUiContainer(url: URL, name: String? = null, updated: Date? = null): FeedUiContainer {
    XmlReader(url.finalUrl()).use {
        val syndFeed = SyndFeedInput().build(it)
        val author = syndFeed.author ?: itunesAuthor(syndFeed) ?: syndFeed.generator ?: url.toString()
        var icon: Bitmap? = null
        val iconUrl = syndFeed.icon?.url ?: syndFeed.image?.url
        if (iconUrl != null) {
            URL(iconUrl).openStream().use {
                icon = BitmapFactory.decodeStream(it)
            }
        }
        val description = syndFeed.description
        return FeedUiContainer(name?: syndFeed.title, author, icon, description, url, updated)
    }
}

fun loadFeedUiContainer(feed: Feed) = loadFeedUiContainer(feed.url, feed.name, feed.lastUpdate)

private fun itunesAuthor(syndFeed: SyndFeed): String? {
    return syndFeed.foreignMarkup.filter { it.name == "author" }.map { it.value }.lastOrNull()
}

fun items(url: URL, since: Date): List<FeedItem> {
    XmlReader(url).use {
        val syndFeed = SyndFeedInput().build(XmlReader(url))
        return syndFeed.entries.map(::item2FeedItem).filter { it.date.after(since) }
    }
}

private fun item2FeedItem(entry: SyndEntry): FeedItem {
    val title = entry.title
    val description = entry.description.value
    val link = if (entry.link != null) URL(entry.link) else null
    val date = entry.publishedDate
    return FeedItem(title, description, link, date)
}