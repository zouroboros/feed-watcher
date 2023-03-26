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
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * @author zouroboros
 */
class TextsTests {
    @Test
    fun findUrlTest1() {
        val ddgUrlString = "https://ddg.gg"
        val ddg = Texts.findUrl(ddgUrlString)
        assertNotNull(ddg, "A URL string should result in a valid url")
        assertEquals(ddg.toString(), ddgUrlString, "findUrl on a valid string should produce the string as URL")
    }

    @Test
    fun findUrlTest2() {
        val urlWithText = "Hello: https://github.com"
        val url = Texts.findUrl(urlWithText)
        assertNotNull(url, "A text with a url should produce the url")
        assertEquals(url.toString(), "https://github.com", "Complete URL should be extracted")
    }

    @Test
    fun findUrlTest3() {
        val textWithNoUrl = "hello:/"

        val url = Texts.findUrl(textWithNoUrl)

        assertNull(url, "Text without an url should not produce a url")
    }

    @Test
    fun findUrlTest4() {
        val textWithMultipleUrls = """Test Pool
https://example.org/podcasts/506

RSS address: https://example.org/podcasts/506/feed.xml"""

        val url = Texts.findUrl(textWithMultipleUrls)

        assertEquals(url.toString(), "https://example.org/podcasts/506", "The first url should be returned")
    }
}