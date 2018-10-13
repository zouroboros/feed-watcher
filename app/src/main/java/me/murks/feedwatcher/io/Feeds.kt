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

fun loadFeedUiContainer(url: URL): FeedUiContainer {
    val syndFeed = SyndFeedInput().build(XmlReader(url.finalUrl()))
    val author = syndFeed.author ?: itunesAuthor(syndFeed) ?: syndFeed.generator ?: url.toString()
    var icon: Bitmap? = null
    val iconUrl = syndFeed.icon?.url ?: syndFeed.image?.url
    if (iconUrl != null) {
        icon = BitmapFactory.decodeStream(URL(iconUrl).openStream())
    }
    val description = syndFeed.description
    return FeedUiContainer(syndFeed.title, author, icon, description, url)
}

fun loadFeedUiContainer(feed: Feed) = loadFeedUiContainer(feed.url)

private fun itunesAuthor(syndFeed: SyndFeed): String? {
    return syndFeed.foreignMarkup.filter { it.name == "author" }.map { it.value }.lastOrNull()
}

fun items(url: URL, since: Date): List<FeedItem> {
    val syndFeed = SyndFeedInput().build(XmlReader(url))
    return syndFeed.entries.map(::item2FeedItem).filter { it.date.after(since) }
}

private fun item2FeedItem(entry: SyndEntry): FeedItem {
    val title = entry.title
    val description = entry.description.value
    val link = if (entry.link != null) URL(entry.link) else null
    val date = entry.publishedDate
    return FeedItem(title, description, link, date)
}