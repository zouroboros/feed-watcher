package me.murks.feedwatcher.atomrss

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.kxml2.io.KXmlParser
import java.io.ByteArrayInputStream
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author zouroboros
 */
class FeedParserTests {
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
       assertEquals(URI.create("https://example.org/image"),
               feedIO.iconUrl)
    }

    @Test
    fun testFeedItems() {
        var source = ByteArrayInputStream(testFeed2.toByteArray())
        var feedIO = FeedParser(source, KXmlParser())

        val formatter = SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss Z")

        var date = formatter.parse("Wed, 31 Jul 2019 21:53:26 +0200")!!

        var feedItems = arrayOf(FeedItem("Item title", "Item description", URI.create("http://example.org/feed/item"), date))
        assertArrayEquals(feedItems, feedIO.items(Date(0)).toTypedArray())

        source = ByteArrayInputStream(testFeed3.toByteArray())
        feedIO = FeedParser(source, KXmlParser())

        date = formatter.parse("Wed, 31 Jul 2019 21:53:26 UTC")!!

        feedItems = arrayOf(FeedItem("Item title", "Item description", URI.create("http://example.org/feed/item"), date),
                FeedItem("Item 2", "<p>Redirecting small portion of subsidies would unleash clean energy revolution, says report</p>",
                        URI.create("https://example.org/feed/item2"), formatter.parse("Thu, 01 Aug 2019 08:20:04 GMT")!!),
                FeedItem("Item 3", "<p>Summer</p>",
                        URI.create("https://example.org/feed/item3"), formatter.parse("Wed, 29 May 2019 00:00:00 UTC")!!))
        assertArrayEquals(feedItems, feedIO.items(Date(0)).toTypedArray())
    }

    @Test
    fun testItunesTags() {
        val source = ByteArrayInputStream(testFeed4.toByteArray())
        val feedIO = FeedParser(source, KXmlParser())

        assertEquals("ITunes Test", feedIO.name)
        assertEquals(URI.create("https://example.org/itunes.png"), feedIO.iconUrl)
        assertEquals("ITunes description", feedIO.description)
    }

    @Test
    fun testAtomFeed() {
        val source = ByteArrayInputStream(atomFeed1.toByteArray())
        val feedIO = FeedParser(source, KXmlParser())
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        val date = formatter.parse("2020-10-17T17:11:00+02:00")!!
        val entries = arrayOf(FeedItem("Atom Entry", "Hello World",
            URI.create("https://www.example.org/folder/article"), date))

        assertEquals("Atom test", feedIO.name)
        assertEquals("Atom subtitle/description", feedIO.description)
        assertEquals(URI.create("https://www.example.de/img/icon-512.png"), feedIO.iconUrl)
        assertArrayEquals(entries, feedIO.items(Date(0)).toTypedArray())
    }

    @Test
    fun testFeed5() {
        val source = ByteArrayInputStream(testFeed5.toByteArray())
        val feedIO = FeedParser(source, KXmlParser())
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz")
        val date = formatter.parse("2021-09-23T13:15:03UTC")!!
        val entries = arrayOf(FeedItem("No alerts in effect, City of Toronto", "No alerts in effect",
            URI.create("https://www.weather.gc.ca/warnings/report_e.html?on61"), date))

        assertEquals("City of Toronto - Weather Alert - Environment Canada", feedIO.name)
        assertEquals(null, feedIO.description)
        assertEquals(URI.create("https://www.weather.gc.ca/template/gcweb/v5.0.1/assets/favicon.ico"), feedIO.iconUrl)
        assertArrayEquals(entries, feedIO.items(Date(0)).toTypedArray())
    }

    @Test
    fun testFeed6() {
        val source = ByteArrayInputStream(testFeed6.toByteArray())
        val feedIO = FeedParser(source, KXmlParser())
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        val date = formatter.parse("2021-11-24T21:28:30+03:00")!!
        val entries = arrayOf(FeedItem("Update abc to 3.4.2", null,
            URI.create("https://example.gitlab.com/org/repo/-/commit/1234"), date))

        assertEquals("Data:master commits", feedIO.name)
        assertEquals(null, feedIO.description)
        assertEquals(null, feedIO.iconUrl)
        assertArrayEquals(entries, feedIO.items(Date(0)).toTypedArray())
    }

    @Test
    fun testFeed7() {
        val source = ByteArrayInputStream(testFeed7.toByteArray())
        val feedIO = FeedParser(source, KXmlParser())
        assertEquals("&", feedIO.items(Date()).first().title)
    }

    @Test
    fun testHtmlInTitle() {
        var source = ByteArrayInputStream(testHtmlInTitle.toByteArray())
        var feedIO = FeedParser(source, KXmlParser())
        assertEquals("<div xmlns=\"http://www.w3.org/1999/xhtml\"><b>&amp;</b></div>", feedIO.name)

        source = ByteArrayInputStream(testHtmlInTitleNestedTitle.toByteArray())
        feedIO = FeedParser(source, KXmlParser())
        assertEquals("<div xmlns=\"http://www.w3.org/1999/xhtml\"><title>Nested</title></div>", feedIO.name)

        source = ByteArrayInputStream(testMarkupInTitleSpecialCharactersInAttributes.toByteArray())
        feedIO = FeedParser(source, KXmlParser())
        assertEquals("<div name=\"test&amp;\">Hello</div>", feedIO.name)

        source = ByteArrayInputStream(testNakedMarkup.toByteArray())
        feedIO = FeedParser(source, KXmlParser())
        assertEquals("Example <b>Atom</b>", feedIO.items(Date(0)).first().description)
    }

    @Test
    fun testLinks() {
        var source = ByteArrayInputStream(testRelativeLinkInEntry.toByteArray())
        var feedIO = FeedParser(source, KXmlParser())

        assertEquals("/relative/link",
                feedIO.items(Date(0)).first().link.toString())
    }

    @Test
    fun testItemWithBodyElement() {
        var source = ByteArrayInputStream(itemWithBodyElement.toByteArray())
        var feedIO = FeedParser(source, KXmlParser())

        assertEquals("""<a href="/relative/uri">click here</a>""",
                feedIO.items(Date(0)).first().description)
    }

    @Test
    fun testItunesChannelImage() {
        var source = ByteArrayInputStream(itunesChannelImage.toByteArray())
        var feedIO = FeedParser(source, KXmlParser())

        assertEquals(URI.create("""http://example.com/logo.jpg"""), feedIO.iconUrl)
    }

    @Test
    fun testThreadingExtension() {
        var source = ByteArrayInputStream(threadingExtension.toByteArray())
        var feedIO = FeedParser(source, KXmlParser())

        assertEquals(1, feedIO.items(Date(0)).size)
        assertEquals(Date(1260320342544), feedIO.items(Date(0)).first().date)
    }

    @Test
    fun testEntityInDoctype() {
        var source = ByteArrayInputStream(entityInDoctype.toByteArray())
        var feedIO = FeedParser(source, KXmlParser())

        feedIO.name
    }

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

    val testFeed6 = """<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom" xmlns:media="http://search.yahoo.com/mrss/">
<title>Data:master commits</title>
<link href="https://gitlab.com/org/repo/-/commits/master?feed_token=1234&amp;format=atom" rel="self" type="application/atom+xml"/>
<link href="https://gitlab.com/org/repo/-/commits/master" rel="alternate" type="text/html"/>
<id>https://gitlab.com/org/repo/-/commits/master</id>
<updated>2021-11-24T21:28:30+03:00</updated>
<entry>
  <id>https://gitlab.com/org/repo/-/commit/1234</id>
  <link href="https://example.gitlab.com/org/repo/-/commit/1234"/>
  <title>Update abc to 3.4.2</title>
  <updated>2021-11-24T21:28:30+03:00</updated>
  <media:thumbnail width="40" height="40" url="https://example.org/12345"/>
  <author>
    <name>relan</name>
    <email>email@hidden</email>
  </author>
  <summary type="html"></summary>
</entry>
</feed>"""

    val testFeed7 = """<feed version="0.3" xmlns="http://purl.org/atom/ns#">
<entry>
	  <title type="text/html" mode="escaped">&amp;</title>
</entry>
</feed>"""

    val testHtmlInTitle = """
<feed version="0.3" xmlns="http://purl.org/atom/ns#">
	  <title type="application/xhtml+xml" mode="xml"><div xmlns="http://www.w3.org/1999/xhtml"><b>&amp;</b></div></title>
</feed>
    """

    val testMarkupInTitleSpecialCharactersInAttributes = """
<feed version="0.3" xmlns="http://purl.org/atom/ns#">
	  <title type="application/xhtml+xml" mode="xml"><div name="test&amp;">Hello</div></title>
</feed>
    """

    val testHtmlInTitleNestedTitle = """
<feed version="0.3" xmlns="http://purl.org/atom/ns#">
	  <title type="application/xhtml+xml" mode="xml"><div xmlns="http://www.w3.org/1999/xhtml"><title>Nested</title></div></title>
</feed>
    """

    val testNakedMarkup = """
 <feed version="0.3" xmlns="http://purl.org/atom/ns#">
     <entry>
          <summary>Example <b>Atom</b></summary>
      </entry>
 </feed>
    """.trimIndent()

    val testRelativeLinkInEntry = """
<feed version="0.3" xmlns="http://purl.org/atom/ns#">
    <entry>
        <link rel="alternate" type="text/html" href="/relative/link"/>
    </entry>
</feed>
    """.trimIndent()

    val itemWithBodyElement = """
<rss version="2.0">
    <channel>
        <item>
            <body><a href="/relative/uri">click here</a></body>
        </item>
    </channel>
</rss>"""

    val itunesChannelImage = """
<rss xmlns:itunes="http://www.itunes.com/DTDs/Podcast-1.0.dtd">
    <channel>
        <itunes:image href="http://example.com/logo.jpg"></itunes:image>
    </channel>
</rss>"""

    val threadingExtension = """
<?xml-stylesheet href="http://www.blogger.com/styles/atom.css" type="text/css"?>
    <feed xmlns='http://www.w3.org/2005/Atom' xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/'>
        <id>tag:blogger.com,1999:blog-893591374313312737.post3861663258538857954..comments</id>
        <updated>2009-12-08T16:59:02.563-08:00</updated>
        <title type='text'>Comments on salmon-test: Test post</title>
        <link rel='http://schemas.google.com/g/2005#feed' type='application/atom+xml' href='http://salmon-test.blogspot.com/feeds/3861663258538857954/comments/default'/>
        <link rel='self' type='application/atom+xml' href='http://www.blogger.com/feeds/893591374313312737/3861663258538857954/comments/default'/>
        <link rel='alternate' type='text/html' href='http://salmon-test.blogspot.com/2009/10/test-post.html'/>
        <link rel='next' type='application/atom+xml' href='http://www.blogger.com/feeds/893591374313312737/3861663258538857954/comments/default?start-index=26&amp;max-results=25'/>
        <author><name>John</name><email>noreply@blogger.com</email></author>
        <generator version='7.00' uri='http://www.blogger.com'>Blogger</generator>
        <openSearch:totalResults>30</openSearch:totalResults>
        <openSearch:startIndex>1</openSearch:startIndex>
        <openSearch:itemsPerPage>25</openSearch:itemsPerPage>
        <entry>
            <id>tag:blogger.com,1999:blog-893591374313312737.post-4788628857625737701</id>
            <published>2009-12-08T16:59:02.544-08:00</published>
            <updated>2009-12-08T16:59:02.544-08:00</updated>
            <title type='text'>bloffo bliff by te...</title>
            <content type='html'>bloffo bliff by &lt;a href="http://example.org/profile/te..." rel="nofollow"&gt;te...&lt;/a&gt;</content>
            <link rel='edit' type='application/atom+xml' href='http://www.blogger.com/feeds/893591374313312737/3861663258538857954/comments/default/4788628857625737701'/>
            <link rel='self' type='application/atom+xml' href='http://www.blogger.com/feeds/893591374313312737/3861663258538857954/comments/default/4788628857625737701'/>
            <link rel='alternate' type='text/html' href='http://salmon-test.blogspot.com/2009/10/test-post.html?showComment=1260320342544#c4788628857625737701' title=''/>
            <author><name>John</name><uri>http://www.blogger.com/profile/12344017489797258795</uri><email>noreply@blogger.com</email><gd:extendedProperty xmlns:gd='http://schemas.google.com/g/2005' name='OpenSocialUserId' value='07886630143387711930'/></author><thr:in-reply-to xmlns:thr='http://purl.org/syndication/thread/1.0' href='http://salmon-test.blogspot.com/2009/10/test-post.html' ref='tag:blogger.com,1999:blog-893591374313312737.post-3861663258538857954' source='http://www.blogger.com/feeds/893591374313312737/posts/default/3861663258538857954' type='text/html'/>
        </entry>
    </feed>
    """.trimIndent()

    val entityInDoctype = """
<!DOCTYPE rss [
  <!ENTITY id "tag:example.com,">
]>
<rss version="2.0">
    <channel>
        <item>
            <guid isPermaLink='false'>&id;2006-05-04:/blog/</guid>
        </item>
    </channel>
</rss>"""
}