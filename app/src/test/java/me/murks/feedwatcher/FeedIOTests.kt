package me.murks.feedwatcher

import junit.framework.Assert.*
import me.murks.feedwatcher.io.FeedIO
import org.junit.Test
import java.io.ByteArrayInputStream
import java.net.URL

/**
 * @author zouroboros
 */
class FeedIOTests {
    val testFeed1 = """
<rss version="2.0">
    <channel>
        <title>testFeed1</title>
        <link>https://example.org</link>
        <description>testFeed1 description</description>
        <language>de-de</language>
    </channel>
</rss>
"""

    val testFeed2 = """
<rss version="2.0">
    <channel>
        <title>testFeed2</title>
        <link>https://example.org</link>
        <description>testFeed2 description</description>
        <image>
            <url>https://example.org/image</url>
        </image>
        <language>en-en</language>
    </channel>
</rss>        
"""
    @Test
    fun testFeedName() {
        val source = ByteArrayInputStream(testFeed1.toByteArray())
        val feedIO = FeedIO(source)
        assertEquals("testFeed1",
                feedIO.feedUiContainer(URL("http://example.org")).name)
    }

    @Test
    fun testFeedDescription() {
        var source = ByteArrayInputStream(testFeed1.toByteArray())
        var feedIO = FeedIO(source)
        assertEquals("testFeed1 description",
                feedIO.feedUiContainer(URL("http://example.org")).description)

        source = ByteArrayInputStream(testFeed2.toByteArray())
        feedIO = FeedIO(source)
        assertEquals("testFeed2 description",
                feedIO.feedUiContainer(URL("http://example.org")).description)
    }

   @Test
    fun testFeedIcon() {
        var source = ByteArrayInputStream(testFeed1.toByteArray())
        var feedIO = FeedIO(source)
        assertEquals(null,
                feedIO.feedUiContainer(URL("http://example.org")).icon)

       source = ByteArrayInputStream(testFeed2.toByteArray())
       feedIO = FeedIO(source)
       assertEquals(URL("https://example.org/image"),
               feedIO.feedUiContainer(URL("http://example.org")).icon)
    }
}