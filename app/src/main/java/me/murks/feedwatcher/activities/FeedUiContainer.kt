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
Copyright 2019 - 2021 Zouroboros
 */
package me.murks.feedwatcher.activities

import me.murks.feedwatcher.atomrss.FeedParser
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.model.Scan
import java.net.URL
import java.util.*

/**
 * Container holding all information shown in the ui for a feed
 * @author zouroboros
 */
// TODO optimize for lazyfeed parser
data class FeedUiContainer(val feed: Feed?, val name: String, val icon: URL?,
                           val description: String?, val url: URL, val updated: Date?,
                           val scans: Collection<Scan>) {

    constructor(feed: Feed, feedParser: FeedParser, scans: Collection<Scan>):
            this(feed, feed.name, feedParser.iconUrl, feedParser.description, feed.url, feed.lastUpdate, scans)

    constructor(feed: Feed, scans: Collection<Scan>):
            this(feed, feed.name, null, null, feed.url, feed.lastUpdate, scans)

    constructor(url: URL, updated: Date?, feed: FeedParser):
            this(null, feed.name, feed.iconUrl, feed.description, url, updated, emptyList())
}