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
along with FeedWatcher. If not, see <https://www.gnu.org/licenses/>.
Copyright 2020 - 2021 Zouroboros
 */
package me.murks.feedwatcher.atomrss

import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.io.StringWriter
import java.net.URI
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.stream.XMLOutputFactory

/**
 * Class for loading data from rss or atom feeds.
 * @author zouroboros
 */
class FeedParser(inputStream: InputStream, parser: XmlPullParser) {

    init {
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)
    }

    private var feedName: String? = null
    private var feedDescription: String? = null
    private var feedIconUrl: URI? = null
    private val entries = LinkedList<FeedItem>()

    private var itemTitle: String? = null
    private var itemDescription: String? = null
    private var itemLink: URI? = null
    private var itemDate: Date? = null

    private val states = listOf(
            ParserNode("rss", {} ,
                listOf(ParserNode("channel", {},
                        listOf(
                            ParserNode("title",
                                readMarkup("title") { text -> feedName = text }),
                            ParserNode("description",
                                readMarkup("description") { text -> feedDescription = text }),
                            ParserNode("itunes:summary", { p -> feedDescription = p.nextText()}),
                            ParserNode("image", {}, listOf(ParserNode("url", { p -> feedIconUrl = parseUrl(p.nextText())}))),
                            ParserNode("itunes:image", { p -> feedIconUrl = parseUrl(p.getAttributeValue(null, "href"))}),
                            ParserNode("item", { p ->
                                    if(p.eventType == XmlPullParser.END_TAG && p.name == "item") {
                                        entries.add(FeedItem(itemTitle, itemDescription, itemLink, itemDate))
                                    }
                                }, listOf(
                                    ParserNode("title", { p -> itemTitle = p.nextText()}),
                                    ParserNode("description",
                                        readMarkup("description")
                                            { text -> itemDescription = text }),
                                    ParserNode("link",
                                        { p -> itemLink = parseUrl(p.nextText())}),
                                    ParserNode("pubDate",
                                        { p -> itemDate = tryReadDate(p.nextText())}),
                                    ParserNode("body", readMarkup("body")
                                        { text -> itemDescription = text}))))))),
            ParserNode("feed", {}, listOf(
                    ParserNode("title", readMarkup("title") { text -> feedName = text }),
                    ParserNode("subtitle", readMarkup("subtitle") { text -> feedDescription = text }),
                    ParserNode("icon", { p -> feedIconUrl = parseUrl(p.nextText())}),
                    ParserNode("logo", { p -> feedIconUrl = parseUrl(p.nextText())}),
                    ParserNode("entry", { p ->
                            if(p.eventType == XmlPullParser.END_TAG && p.name == "entry") {
                                entries.add(FeedItem(itemTitle, itemDescription, itemLink, itemDate))
                            }
                        }, listOf(ParserNode("title",
                            readMarkup("title") { itemTitle = it }),
                                ParserNode("summary",
                                    readMarkup("summary") { text -> itemDescription = text }),
                                ParserNode("media:description",
                                    readMarkup("media:description") { text -> itemDescription = text }),
                                ParserNode("link", { p ->
                                    if(p.eventType == XmlPullParser.START_TAG && p.name == "link") {
                                        itemLink = parseUrl(p.getAttributeValue(null, "href"))
                                    }}),
                                ParserNode("published", { p -> itemDate = tryReadDate(p.nextText())}),
                                ParserNode("updated", { p -> itemDate = tryReadDate(p.nextText())}))))))

    private val parser = LazyParser(parser, states)

    val name: String?
        get() {
            if(feedName == null) {
                parser.parseUntil { feedName != null }
            }

            return feedName
        }

    val iconUrl: URI?
        get() {
            if(feedIconUrl == null) {
                parser.parseUntil { feedIconUrl != null }
            }

            return feedIconUrl
        }

    val description: String?
        get() {
            if(feedDescription == null) {
                parser.parseUntil { feedDescription != null }
            }

            return feedDescription
        }

    fun items(since: Date): List<FeedItem> {
        parser.parseUntil { false }
        return entries.filter { it.date == null || it.date.after(since) }
    }

    @Suppress("SimpleDateFormat")
    private fun tryReadDate(dateStr: String): Date {

        // replace nonstandard time zone identifier
        val str = dateStr.replace("UT", "UTC").replace("Z", "UTC")

        val formats = listOf(SimpleDateFormat("EEE, dd MMM yy HH:mm:ss z", Locale.ENGLISH),
                SimpleDateFormat("EEE, dd MMM yy HH:mm z"),
                SimpleDateFormat("dd MMM yy HH:mm:ss z"),
                SimpleDateFormat("dd MMM yy HH:mm z"),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz")
        )

        for (format in formats) {
            try {
                return format.parse(str.trim())!!
            } catch (e: ParseException) { }
        }

        throw ParseException(str, 0)
    }

    private fun readMarkup(elementName: String, setter: (String?) -> Unit): (XmlPullParser) -> Unit
    = { p ->
        p.next()
        val startAndLength = IntArray(2)
        val writer = StringWriter()
        val xmlWriter = XMLOutputFactory.newFactory()
            .createXMLStreamWriter(writer)
        var text: String? = null
        var hasMarkup = false
        var nestedCounter = 0
        while(p.name != elementName || p.eventType != XmlPullParser.END_TAG || nestedCounter != 0) {
            when (p.eventType) {
                XmlPullParser.START_TAG -> {
                    hasMarkup = true
                    xmlWriter.writeStartElement(p.name)
                    for (i in 0 until p.attributeCount) {
                        xmlWriter.writeAttribute(p.getAttributeName(i), p.getAttributeValue(i))
                    }
                    if (p.name == elementName) {
                        nestedCounter += 1
                    }
                }
                XmlPullParser.END_TAG -> {
                    xmlWriter.writeEndElement()
                    if (p.name == elementName) {
                        nestedCounter -= 1
                    }
                }
                else -> {
                    val characters = p.getTextCharacters(startAndLength)
                    text = p.text
                    if (characters != null) {
                        xmlWriter.writeCharacters(characters, startAndLength[0], startAndLength[1])
                    }
                }
            }
            p.next()
        }
        if (!hasMarkup) {
            setter(text)
        } else {
            setter(writer.toString())
        }
    }

    private fun parseUrl(text: String): URI? {
        return URI.create(text)
    }
}