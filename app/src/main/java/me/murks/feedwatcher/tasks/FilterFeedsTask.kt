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
Copyright 2019-2020 Zouroboros
 */
package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import android.util.Xml
import me.murks.feedwatcher.*
import me.murks.feedwatcher.io.FeedParser
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

        app.environment.log.info("Filtering feeds.")
        try {
            val client = OkHttpClient()
            for (feed in feeds) {
                app.environment.log.info("Filter feed: ${feed.url}.")
                val request = Request.Builder().url(feed.url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    response.body!!.byteStream().use {
                        stream ->
                        val feedIo = FeedParser(stream, Xml.newPullParser())
                        val items = feedIo.items(feed.lastUpdate?: Date(0))

                        app.environment.log.info("Found ${items.size} new entries.")

                        val matchingItems = queries.associateBy({query -> query},
                                { query ->
                                    query.filter.fold(items) {
                                        acc, filter -> filter.filterItems(feed, acc)}})
                                .entries.map { entry -> entry.value.map {
                                    item -> AbstractMap.SimpleEntry(entry.key, item) } }
                                .flatten()
                                .groupBy({ it.value }) { it.key }

                        app.environment.log.info("Found ${matchingItems.size} new matching entries.")

                        matchingItems.entries.forEach {
                            val result = Result(0, feed, it.value, it.key, Date())
                            publishProgress(result)
                            allResults.add(result)
                        }
                    }
                } else {
                    app.environment.log.error("${feed.url} returned status code ${response.code}.")
                }
            }
            return Right(allResults)
        } catch (ioe: IOException) {
            app.environment.log.error("Error filtering feeds.", ioe)
            return Left(ioe)
        } catch (e: Exception) {
            app.environment.log.error("Error filtering feeds.", e)
            return Left(e)
        } finally {
            app.environment.log.info("Filtering feeds finished.")
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