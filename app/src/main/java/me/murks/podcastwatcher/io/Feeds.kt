package me.murks.podcastwatcher.io

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import me.murks.podcastwatcher.FeedUiContainer
import me.murks.podcastwatcher.model.Feed
import java.io.InputStream
import java.net.URL

/**
 * @author zouroboros
 * @date 8/15/18.
 */
fun loadFeedUiContainer(feed: Feed): FeedUiContainer {
    val syndFeed = SyndFeedInput().build(XmlReader(feed.url))
    val author = syndFeed.author ?: itunesAuthor(syndFeed) ?: syndFeed.generator ?: feed.url.toString()
    var icon: Bitmap? = null
    var iconUrl = syndFeed.icon?.url ?: syndFeed.image?.url
    if (iconUrl != null) {
        icon = BitmapFactory.decodeStream(URL(iconUrl).openStream())
    }
    return FeedUiContainer(feed, syndFeed.title, author, icon)
}

fun itunesAuthor(syndFeed: SyndFeed): String? {
    return syndFeed.foreignMarkup.filter { it.name == "author" }.map { it.value }.lastOrNull()
}