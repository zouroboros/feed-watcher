package me.murks.feedwatcher.io

import me.murks.feedwatcher.model.FeedItem
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.kxml2.io.KXmlParser
import java.io.ByteArrayInputStream
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author zouroboros
 */
class FeedIOTests {
    val testFeed1 = """<?xml version="1.0" encoding="UTF-8" ?>
<?xml-stylesheet href="/resources/xsl/rss2.jsp" type="text/xsl"?>
<rss version="2.0">
    <channel>
        <title>testFeed1</title>
        <link>https://example.org</link>
        <description>testFeed1 description</description>
        <language>de-de</language>
    </channel>
</rss>
"""

    val testFeed2 = """<?xml version="1.0" encoding="UTF-8" ?>
<?xml-stylesheet href="/resources/xsl/rss2.jsp" type="text/xsl"?>
<rss version="2.0" xmlns:content="http://purl.org/rss/1.0/modules/content/">
    <channel>
        <title>testFeed2</title>
        <link>https://example.org</link>
        <description>testFeed2 description</description>
        <image>
            <url>https://example.org/image</url>
        </image>
        <language>en-en</language>
        <item>
            <title>Item title</title>
            <link>http://example.org/feed/item</link>
            <pubDate>Wed, 31 Jul 2019 21:53:26 +0200</pubDate>
            <content:encoded>
            <![CDATA[<p><a href="example.org">example.org</a></p>]]>
            </content:encoded>
            <description>Item description</description>
            <guid>example.org/feed/abcdefg1234566</guid>
        </item>
    </channel>
</rss>        
"""

    @Test
    fun testFeedName() {
        val source = ByteArrayInputStream(testFeed1.toByteArray())
        val feedIO = FeedIO(source, KXmlParser())
        assertEquals("testFeed1",
                feedIO.name)
    }

    @Test
    fun testFeedDescription() {
        var source = ByteArrayInputStream(testFeed1.toByteArray())
        var feedIO = FeedIO(source, KXmlParser())
        assertEquals("testFeed1 description",
                feedIO.description)

        source = ByteArrayInputStream(testFeed2.toByteArray())
        feedIO = FeedIO(source, KXmlParser())
        assertEquals("testFeed2 description",
                feedIO.description)
    }

   @Test
    fun testFeedIcon() {
        var source = ByteArrayInputStream(testFeed1.toByteArray())
        var feedIO = FeedIO(source, KXmlParser())
        assertEquals(null,
                feedIO.iconUrl)

       source = ByteArrayInputStream(testFeed2.toByteArray())
       feedIO = FeedIO(source, KXmlParser())
       assertEquals(URL("https://example.org/image"),
               feedIO.iconUrl)
    }

    @Test
    fun testFeedItems() {
        var source = ByteArrayInputStream(testFeed2.toByteArray())
        var feedIO = FeedIO(source, KXmlParser())

        val formatter = SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss Z")
        val date = formatter.parse("Wed, 31 Jul 2019 21:53:26 +0200")

        val feedItems = arrayOf(FeedItem("Item title", "Item description", URL("http://example.org/feed/item"), date))
        assertArrayEquals(feedItems, feedIO.items(Date(0)).toTypedArray())
    }
}