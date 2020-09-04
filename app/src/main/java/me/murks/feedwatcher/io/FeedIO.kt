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

import me.murks.feedwatcher.model.FeedItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Class for loading data from rss or atom feeds
 * @author zouroboros
 */
// TODO think about lazy parsing, this especially important for the feed items
class FeedIO(inputStream: InputStream, parser: XmlPullParser) {
    private var feedName: String? = null
    private var feedDescription: String? = null
    private var feedIconUrl: URL? = null
    private val entries = LinkedList<FeedItem>()

    val name: String
        get() = feedName!!

    val iconUrl: URL?
        get() = feedIconUrl

    val description
        get() = feedDescription

    init {
        inputStream.use {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            readDocument(parser)
        }
    }

    fun items(since: Date): List<FeedItem> {
        return entries.filter { it.date.after(since) }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readDocument(parser: XmlPullParser) {
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "channel" -> readChannel(parser)
                else -> skip(parser)
            }
        }
    }


    private fun readChannel(parser: XmlPullParser) {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "title" -> feedName = readElementText(parser,"title")
                "description" -> feedDescription = readElementText(parser,"description")
                "itunes:summary" -> feedDescription = readElementText(parser, "itunes:summary")
                "image" -> readIcon(parser)
                "item" -> readItem(parser)
                "itunes:image" -> {
                    try {
                        feedIconUrl = URL(parser.getAttributeValue(null, "href"))
                    } catch (e: MalformedURLException) {}
                }
                else -> skip(parser)
            }
        }
    }

    private fun readItem(parser: XmlPullParser) {
        var title: String? = null
        var description: String? = null
        var link: String? = null
        var date: Date? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "title" -> title = readElementText(parser,"title")
                "description" -> description = readElementText(parser,"description")
                "link" -> link = readElementText(parser,"link")
                "pubDate" -> {
                    val dateStr = readElementText(parser,"pubDate")
                    date = tryReadDate(dateStr)
                }
                else -> skip(parser)
            }
        }
        entries.add(FeedItem(title!!, description!!, URL(link), date!!))
    }

    private fun tryReadDate(dateStr: String): Date {

        // replace nonstandard time zone identifier
        val str = dateStr.replace("UT", "UTC").replace("Z", "UTC")

        val formats = listOf(SimpleDateFormat("EEE, dd MMM yy HH:mm:ss z", Locale.ENGLISH),
                SimpleDateFormat("EEE, dd MMM yy HH:mm z"),
                SimpleDateFormat("dd MMM yy HH:mm:ss z"),
                SimpleDateFormat("dd MMM yy HH:mm z"))

        for (format in formats) {
            try {
                return format.parse(str.trim())
            } catch (e: ParseException) { }
        }

        throw ParseException(str, 0)
    }

    private fun readIcon(parser: XmlPullParser) {
        parser.require(XmlPullParser.START_TAG, null, "image")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "url" -> {
                    try {
                        feedIconUrl = URL(readElementText(parser,"url"))
                    } catch (mue: MalformedURLException) { }
                }
                else -> skip(parser)
            }
        }
    }

    private fun readElementText(parser: XmlPullParser, element: String): String {
        parser.require(XmlPullParser.START_TAG, null, element)
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, null, element)
        return title
    }


    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }


    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}