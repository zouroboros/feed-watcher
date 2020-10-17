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
package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import android.util.Xml
import me.murks.feedwatcher.*
import me.murks.feedwatcher.io.FeedParser
import me.murks.feedwatcher.io.LazyFeedParser
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.model.Result
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*

/**
 * @author zouroboros
 */
class FilterFeedsTask(private val app: FeedWatcherApp,
                      private val listener: TaskListener<Result, Either<Exception, List<Result>>>)
    : AsyncTask<Feed, Result, Either<Exception, List<Result>>>() {

    override fun doInBackground(vararg feeds: Feed): Either<Exception, List<Result>> {
        val queries = app.queries()

        val allResults = LinkedList<Result>()

        try {
            val client = OkHttpClient()
            for (feed in feeds) {
                val request = Request.Builder().url(feed.url).build()
                client.newCall(request).execute().body!!.byteStream().use {
                    val items = queries.associateBy({query -> query},
                            { query ->
                                val feedIo = LazyFeedParser(it, Xml.newPullParser())
                                query.filter.fold(feedIo.items(feed.lastUpdate?: Date(0)))
                                {acc, filter -> filter.filterItems(feed, acc)}})
                            .entries.map { it.value.map {
                                item -> AbstractMap.SimpleEntry(it.key, item) } }
                            .flatten()
                            .groupBy({ it.value }) { it.key }

                    items.entries.forEach {
                        val result = Result(0, feed, it.value, it.key, Date())
                        publishProgress(result)
                        allResults.add(result)
                    }
                }
            }
            return Right(allResults)
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            return Left(ioe)
        } catch (e: Exception) {
            return Left(e)
        }
    }

    override fun onPostExecute(result: Either<Exception, List<Result>>) {
        listener.onResult(result)
        super.onPostExecute(result)
    }

    override fun onProgressUpdate(vararg values: Result) {
        for (result in values) {
            listener.onProgress(result)
        }
        super.onProgressUpdate(*values)
    }
}