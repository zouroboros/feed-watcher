package me.murks.feedwatcher.io

import me.murks.feedwatcher.model.FeedItem
import org.xmlpull.v1.XmlPullParser
import java.io.Closeable
import java.io.InputStream
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author zouroboros
 */
class LazyFeedParser(inputStream: InputStream, parser: XmlPullParser) {

    init {
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)
    }

    private var feedName: String? = null
    private var feedDescription: String? = null
    private var feedIconUrl: URL? = null
    private val entries = LinkedList<FeedItem>()

    private var itemTitle: String? = null
    private var itemDescription: String? = null
    private var itemLink: String? = null
    private var itemDate: Date? = null

    private val states = listOf(
            ParserState("rss", {} ,
                listOf(ParserState("channel", {},
                        listOf(
                            ParserState("title", { p -> feedName = p.nextText() }),
                            ParserState("description", { p -> feedDescription = p.nextText()}),
                            ParserState("itunes:summary", { p -> feedDescription = p.nextText()}),
                            ParserState("image", {}, listOf(ParserState("url", { p -> feedIconUrl = URL(p.nextText())}))),
                            ParserState("itunes:image", { p -> feedIconUrl = URL(p.getAttributeValue(null, "href"))}),
                            ParserState("item", { p ->
                                    if(p.eventType == XmlPullParser.END_TAG && p.name == "item") {
                                        entries.add(FeedItem(itemTitle!!, itemDescription!!, if (itemLink != null) URL(itemLink) else null, itemDate!!))
                                    }
                                }, listOf(
                                    ParserState("title", { p -> itemTitle = p.nextText()}),
                                    ParserState("description", { p ->
                                        itemDescription = p.nextText()}),
                                    ParserState("link", { p -> itemLink = p.nextText()}),
                                    ParserState("pubDate", { p -> itemDate = tryReadDate(p.nextText())}))))))),
            ParserState("feed", {}, listOf(
                    ParserState("title", { p -> feedName = p.nextText() }),
                    ParserState("subtitle", { p -> feedDescription = p.nextText()}),
                    ParserState("icon", { p -> feedIconUrl = URL(p.nextText())}),
                    ParserState("logo", { p -> feedIconUrl = URL(p.nextText())}),
                    ParserState("entry", { p ->
                            if(p.eventType == XmlPullParser.END_TAG && p.name == "entry") {
                                entries.add(FeedItem(itemTitle!!, itemDescription!!, if (itemLink != null) URL(itemLink) else null, itemDate!!))
                            }
                        }, listOf(ParserState("title", { p -> itemTitle = p.nextText()}),
                                ParserState("summary", { p -> itemDescription = p.nextText()}),
                                ParserState("media:description", { p -> itemDescription = p.nextText()}),
                                ParserState("link", { p ->
                                    if(p.eventType == XmlPullParser.START_TAG && p.name == "link") {
                                        itemLink = p.getAttributeValue(null, "href")
                                    }}),
                                ParserState("published", { p -> itemDate = tryReadDate(p.nextText())}))))))

    private val parser = LazyParser(parser, states)

    val name: String
        get() {
            if(feedName == null) {
                parser.parseUntil { feedName != null }
            }

            return feedName!!
        }

    val iconUrl: URL?
        get() {
            if(feedIconUrl == null) {
                parser.parseUntil { feedIconUrl != null }
            }

            return feedIconUrl
        }

    val description: String
        get() {
            if(feedDescription == null) {
                parser.parseUntil { feedDescription != null }
            }

            return feedDescription!!
        }

    fun items(since: Date): List<FeedItem> {
        parser.parseUntil { false }
        return entries.filter { it.date.after(since) }
    }

    private fun tryReadDate(dateStr: String): Date {

        // replace nonstandard time zone identifier
        val str = dateStr.replace("UT", "UTC").replace("Z", "UTC")

        val formats = listOf(SimpleDateFormat("EEE, dd MMM yy HH:mm:ss z", Locale.ENGLISH),
                SimpleDateFormat("EEE, dd MMM yy HH:mm z"),
                SimpleDateFormat("dd MMM yy HH:mm:ss z"),
                SimpleDateFormat("dd MMM yy HH:mm z"),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        )

        for (format in formats) {
            try {
                return format.parse(str.trim())
            } catch (e: ParseException) { }
        }

        throw ParseException(str, 0)
    }
}