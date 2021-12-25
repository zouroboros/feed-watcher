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
package me.murks.feedwatcher.tasks

import android.util.Xml
import me.murks.feedwatcher.Either
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.Right
import me.murks.feedwatcher.atomrss.FeedParser
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.model.Query
import me.murks.feedwatcher.model.Result
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception
import java.util.*

typealias FilterResult = Either<Pair<Feed, Exception>, Pair<Feed, Collection<Result>>>

/**
 * Object which contains method for filtering feeds
 * @author zouroboros
 */
object FeedsFilter {
    fun filterFeeds(feeds: Collection<Feed>, queries: Collection<Query>): Sequence<Either<Pair<Feed, Exception>, Pair<Feed, Collection<Result>>>> {
        val client = OkHttpClient()
        return feeds.asSequence().map {feed ->
           try {
               val request = Request.Builder().url(feed.url).build()
               val response = client.newCall(request).execute()

               if (response.isSuccessful) {
                   response.body!!.use {
                       it.byteStream().use {
                           stream ->
                           val feedIo = FeedParser(stream, Xml.newPullParser(), Xml.newSerializer())
                           val items = feedIo.items(feed.lastUpdate?: Date(0))
                               // we only want items with a date.
                               // this could be done more intelligently but for now we rely on the
                               // feeds to provde a date.
                               .filter { it.date != null }

                           val matchingItems = queries.associateBy({query -> query},
                                   { query ->
                                       query.filter.fold(items) {
                                           acc, filter -> filter.filterItems(feed, acc)}})
                                   .entries.map { entry -> entry.value.map {
                                       item -> AbstractMap.SimpleEntry(entry.key, item) } }
                                   .flatten()
                                   .groupBy({ it.value }) { it.key }

                           val results = matchingItems.entries.map { Result(0, feed, it.value, it.key, Date()) }
                           return@map Right(Pair(feed, results))
                       }
                   }
               } else {
                   return@map Left(Pair(feed, IOException("${feed.url} returned status code ${response.code}.")))
               }

           } catch (e: Exception) {
                return@map Left(Pair(feed, e))
           }
       }
    }
}