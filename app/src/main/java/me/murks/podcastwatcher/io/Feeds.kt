package me.murks.podcastwatcher.io

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import me.murks.podcastwatcher.activities.FeedUiContainer
import me.murks.podcastwatcher.model.Feed
import java.net.URL

/**
 * @author zouroboros
 * @date 8/15/18.
 */

fun loadFeedUiContainer(url: URL): FeedUiContainer {
    val syndFeed = SyndFeedInput().build(XmlReader(url))
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