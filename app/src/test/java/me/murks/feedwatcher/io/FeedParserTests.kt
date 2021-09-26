package me.murks.feedwatcher.io

import me.murks.feedwatcher.model.FeedItem
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.kxml2.io.KXmlParser
import java.io.ByteArrayInputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author zouroboros
 */
class FeedParserTests {
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
    val testFeed3 = """<?xml version="1.0" encoding="UTF-8" ?>
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
            <pubDate>Wed, 31 Jul 2019 21:53:26 Z</pubDate>
            <content:encoded>
            <![CDATA[<p><a href="example.org">example.org</a></p>]]>
            </content:encoded>
            <description>Item description</description>
            <guid>example.org/feed/abcdefg1234566</guid>
        </item>
        <item>
            <title>Item 2</title>
            <link>https://example.org/feed/item2</link>
            <description>&lt;p&gt;Redirecting small portion of subsidies would unleash clean energy revolution, says report&lt;/p&gt;</description>
            <pubDate>Thu, 01 Aug 2019 08:20:04 GMT</pubDate>
            <guid>https://example.org/feed/item2</guid>
            <media:content width="140" url="https://example.org/media">
                <media:credit scheme="urn:ebu">Test Photographer</media:credit>
            </media:content>
            <media:content width="460" url="https://example.org/img2">
                <media:credit scheme="urn:ebu">Test Photographer 2</media:credit>
            </media:content>
            <dc:creator>Creatorios</dc:creator>
            <dc:date>2019-08-01T08:20:04Z</dc:date>
        </item>
        <item>
            <title>Item 3</title>
            <link>https://example.org/feed/item3</link>
            <description>&lt;p&gt;Summer&lt;/p&gt;</description>
            <pubDate>Wed, 29 May 2019 00:00:00 UT</pubDate>
            <guid>https://example.org/feed/item3</guid>        
        </item>
    </channel>
</rss>        
"""
    val testFeed4 = """
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" media="screen" href="/~d/styles/rss2full.xsl"?><?xml-stylesheet type="text/css" media="screen" href="http://rss.cnn.com/~d/styles/itemcontent.css"?><rss xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:feedburner="http://rssnamespace.org/feedburner/ext/1.0" version="2.0" xml:lang="en-US"><channel>
<ttl>240</ttl>
<title>ITunes Test</title>
<link>example.org/itunes-rss</link>
<language>en-us</language>
<copyright>Copyright</copyright>
<itunes:author>ITunes author</itunes:author>
<itunes:summary>ITunes description</itunes:summary>
<itunes:owner>
    <itunes:name>ITunes name</itunes:name>
    <itunes:email>podcast@example.org</itunes:email>
</itunes:owner>
<itunes:explicit>No</itunes:explicit>
<itunes:image href="https://example.org/itunes.png" />
<itunes:category text="Education" />
<itunes:new-feed-url>https://example.org/itunes-new-feed-url</itunes:new-feed-url>
<atom10:link xmlns:atom10="http://www.w3.org/2005/Atom" rel="self" type="application/rss+xml" href="https://example.org/itunes" />
<feedburner:info uri="services/podcasting/itunes/rss" />
<atom10:link xmlns:atom10="http://www.w3.org/2005/Atom" rel="hub" href="http://pubsubhubbub.appspot.com/" />
<item>
    <title>ITunes Test Item</title>
    <link>https://example/itunes/item</link>
    <description>Itunes test item</description>
    <dc:creator>Itunes test</dc:creator>
    <category>Education</category>
    <enclosure url="https://example.orf/enclosure" length="" type="video/mp4" />
    <pubDate>Thu, 05 Sep 2019 18:25:24 EDT</pubDate>
    <guid isPermaLink="false">https://example/itunes/item</guid>
    <itunes:author>Test</itunes:author>
    <itunes:summary>Itunes item description</itunes:summary>
    <itunes:duration>10:00</itunes:duration>
    <source url="">ITunes Test</source>
    <feedburner:origLink>https://</feedburner:origLink>
</item>
</channel>
</rss>""".trim()


    val atomFeed1 = """
<?xml version="1.0" encoding="UTF-8"?>
<?access-control allow="*"?>
<?xml-stylesheet href="/css/feed-atom.20171024.xsl" type="text/xsl"?>
<feed>
    <title>Atom test</title>
    <subtitle>Atom subtitle/description</subtitle>
    <icon>https://www.example.de/img/icon-512.png</icon>
	<logo>https://www.example.de/img/logo-feed.png</logo>
    <entry>
		<title>Atom Entry</title>
		<link rel="alternate" type="text/html" href="https://www.example.org/folder/article" />
		<id>69278ec4-4865-4604-9230-b7eece14fb79</id>
		<published>2020-10-17T17:11:00+02:00</published>
		<updated>2020-10-17T17:11:00+02:00</updated>
		<summary type="html"><![CDATA[Hello World]]></summary>
		
		<author>
			<name>Entry author</name>
		</author>
		<category term="entries" scheme="https://www.example.org/topics/" />
	</entry>
</feed>
    """.trimIndent()

    val testFeed5 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            " <feed xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-ca\">\n" +
            " \n" +
            "<!--\n" +
            "This is a data file that is meant to be read by RSS readers or aggregators.\n" +
            "See https://www.canada.ca/en/environment-climate-change/services/weather-general-tools-resources/ways-to-access-forecasts.html to learn more about our RSS service.\n" +
            "-->\n" +
            "\n" +
            " <title>City of Toronto - Weather Alert - Environment Canada</title>\n" +
            " <link rel=\"related\" href=\"https://www.weather.gc.ca/warnings/report_e.html?on61\" type=\"text/html\"/>\n" +
            " <link rel=\"self\" href=\"https://www.weather.gc.ca/rss/battleboard/on61_e.xml\" type=\"application/atom+xml\"/>\n" +
            " <link rel=\"alternate\" hreflang=\"fr-ca\" href=\"https://www.meteo.gc.ca/rss/battleboard/on61_f.xml\" type=\"application/atom+xml\"/>\n" +
            " <author>\n" +
            " <name>Environment Canada</name>\n" +
            " <uri>https://www.weather.gc.ca</uri>\n" +
            " </author>\n" +
            " <updated>2021-09-23T13:15:03Z</updated>\n" +
            " <id>tag:weather.gc.ca,2013-04-16:20210923131503</id>\n" +
            " <logo>https://www.weather.gc.ca/template/gcweb/v5.0.1/assets/wmms-alt.png</logo>\n" +
            " <icon>https://www.weather.gc.ca/template/gcweb/v5.0.1/assets/favicon.ico</icon>\n" +
            " <rights>Copyright 2021, Environment Canada</rights>\n" +
            " <entry>\n" +
            " <title>No alerts in effect, City of Toronto</title>\n" +
            " <link type=\"text/html\" href=\"https://www.weather.gc.ca/warnings/report_e.html?on61\"/>\n" +
            " <updated>2021-09-23T13:15:03Z</updated>\n" +
            " <published>2021-09-23T13:15:03Z</published>\n" +
            " <category term=\"Alerts\"/>\n" +
            " <summary type=\"html\">No alerts in effect</summary>\n" +
            " <id>tag:weather.gc.ca,2013-04-16:20210923131503</id>\n" +
            " </entry>\n" +
            " </feed>\n"

    @Test
    fun testFeedName() {
        val source = ByteArrayInputStream(testFeed1.toByteArray())
        val feedIO = FeedParser(source, KXmlParser())
        assertEquals("testFeed1",
                feedIO.name)
    }

    @Test
    fun testFeedDescription() {
        var source = ByteArrayInputStream(testFeed1.toByteArray())
        var feedIO = FeedParser(source, KXmlParser())
        assertEquals("testFeed1 description",
                feedIO.description)

        source = ByteArrayInputStream(testFeed2.toByteArray())
        feedIO = FeedParser(source, KXmlParser())
        assertEquals("testFeed2 description",
                feedIO.description)
    }

   @Test
    fun testFeedIcon() {
        var source = ByteArrayInputStream(testFeed1.toByteArray())
        var feedIO = FeedParser(source, KXmlParser())
        assertEquals(null,
                feedIO.iconUrl)

       source = ByteArrayInputStream(testFeed2.toByteArray())
       feedIO = FeedParser(source, KXmlParser())
       assertEquals(URL("https://example.org/image"),
               feedIO.iconUrl)
    }

    @Test
    fun testFeedItems() {
        var source = ByteArrayInputStream(testFeed2.toByteArray())
        var feedIO = FeedParser(source, KXmlParser())

        val formatter = SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss Z")

        var date = formatter.parse("Wed, 31 Jul 2019 21:53:26 +0200")!!

        var feedItems = arrayOf(FeedItem("Item title", "Item description", URL("http://example.org/feed/item"), date))
        assertArrayEquals(feedItems, feedIO.items(Date(0)).toTypedArray())

        source = ByteArrayInputStream(testFeed3.toByteArray())
        feedIO = FeedParser(source, KXmlParser())

        date = formatter.parse("Wed, 31 Jul 2019 21:53:26 UTC")!!

        feedItems = arrayOf(FeedItem("Item title", "Item description", URL("http://example.org/feed/item"), date),
                FeedItem("Item 2", "<p>Redirecting small portion of subsidies would unleash clean energy revolution, says report</p>",
                        URL("https://example.org/feed/item2"), formatter.parse("Thu, 01 Aug 2019 08:20:04 GMT")!!),
                FeedItem("Item 3", "<p>Summer</p>",
                        URL("https://example.org/feed/item3"), formatter.parse("Wed, 29 May 2019 00:00:00 UTC")!!))
        assertArrayEquals(feedItems, feedIO.items(Date(0)).toTypedArray())
    }

    @Test
    fun testItunesTags() {
        val source = ByteArrayInputStream(testFeed4.toByteArray())
        val feedIO = FeedParser(source, KXmlParser())

        assertEquals("ITunes Test", feedIO.name)
        assertEquals(URL("https://example.org/itunes.png"), feedIO.iconUrl)
        assertEquals("ITunes description", feedIO.description)
    }

    @Test
    fun testAtomFeed() {
        val source = ByteArrayInputStream(atomFeed1.toByteArray())
        val feedIO = FeedParser(source, KXmlParser())
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        val date = formatter.parse("2020-10-17T17:11:00+02:00")!!
        val entries = arrayOf(FeedItem("Atom Entry", "Hello World", URL("https://www.example.org/folder/article"), date))

        assertEquals("Atom test", feedIO.name)
        assertEquals("Atom subtitle/description", feedIO.description)
        assertEquals(URL("https://www.example.de/img/icon-512.png"), feedIO.iconUrl)
        assertArrayEquals(entries, feedIO.items(Date(0)).toTypedArray())
    }

    @Test
    fun testFeed5() {
        val source = ByteArrayInputStream(testFeed5.toByteArray())
        val feedIO = FeedParser(source, KXmlParser())
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz")
        val date = formatter.parse("2021-09-23T13:15:03UTC")!!
        val entries = arrayOf(FeedItem("No alerts in effect, City of Toronto", "No alerts in effect", URL("https://www.weather.gc.ca/warnings/report_e.html?on61"), date))

        assertEquals("City of Toronto - Weather Alert - Environment Canada", feedIO.name)
        assertEquals(null, feedIO.description)
        assertEquals(URL("https://www.weather.gc.ca/template/gcweb/v5.0.1/assets/favicon.ico"), feedIO.iconUrl)
        assertArrayEquals(entries, feedIO.items(Date(0)).toTypedArray())
    }
}