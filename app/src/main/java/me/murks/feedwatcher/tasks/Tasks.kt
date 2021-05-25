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
along with FeedWatcher.  If not, see <https://www.gnu.org/licenses/>.
Copyright 2020 - 2021 Zouroboros
 */
package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Xml
import me.murks.feedwatcher.Either
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.Right
import me.murks.feedwatcher.activities.FeedUiContainer
import me.murks.feedwatcher.io.FeedParser
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.model.Query
import me.murks.feedwatcher.model.Scan
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL
import java.util.concurrent.CompletableFuture

/**
 * Class with utility functions for creating tasks.
 *
 * This class is the central entry
 * @author zouroboros
 */
// TODO migrate everything to completable future
object Tasks {

    /**
     * Creates a task that maps the given parameter in the background.
     * @param mapper Function that maps items. This function in executed in the background.
     * @param consumer Action that consumes the results. This function is executed on the main thread.
     * @param errorHandler Action that is called in case an error occurs in the mapper function.
     * @param onFinished Action that is called when all items have been converted.
     */
    fun <TInput, TOutput> stream(mapper: (TInput) -> TOutput,
                                 consumer: (TOutput) -> Unit,
                                 errorHandler: (TInput, Exception) -> Unit,
                                 onFinished: () -> Unit): StreamingTask<TInput, TOutput> = StreamingTask(mapper, object : StreamingTask.Listener<TInput, TOutput> {
        override fun onResult(item: TOutput) = consumer(item)

        override fun onError(item: StreamingTask.Error<TInput>) = errorHandler(item.item, item.error)

        override fun onFinished() = onFinished()
    })

    /**
     * Creates a task for running a function in background
     * @param func Function to be run in the background.
     * @param consumer Action that consumes the result, executed on the main thread.
     * @param errorHandler Action that is called when an error occured running func.
     */
    fun <TInput, TOutput>run(func: (TInput) -> TOutput,
                             consumer: (TOutput) -> Unit,
                            errorHandler: (Exception) -> Unit) =
            object : AsyncTask<TInput, Void, Either<Exception, TOutput>>() {
                override fun doInBackground(vararg p0: TInput): Either<Exception, TOutput> {
                    try {
                        return Right(func(p0.get(0)))
                    } catch (e: Exception) {
                        return Left(e)
                    }
                }

                override fun onPostExecute(result: Either<Exception, TOutput>)
                        = result.either(errorHandler, consumer)
            }

    fun filterFeeds(app: FeedWatcherApp) =
            CompletableFuture.supplyAsync { FeedsFilter.filterFeeds(app.feeds(), app.queries()) }
            .thenApplyAsync { results ->
                results.forEach { result ->
                    when (result) {
                        is Left -> {
                            val feed = result.value.first
                            val error = result.value.second
                            app.environment.log.error("Error filtering feed ${feed.url}.", error)
                        }
                        is Right -> {
                            val feed = result.value.first
                            val items = result.value.second
                            app.environment.log.info("Found ${items.size} new entries for feed ${feed.url}.")
                        }
                    }
                }
                app.scanResults(results)
                app.environment.log.info("Filtering feeds finished.")
            }.exceptionally {
                app.environment.log.error("Error during feed filtering.", it)
            }

    /**
     * Loads a Url into a FeedUiContainer
     */
    fun loadFeedUiContainer(url: URL, existingFeed: Pair<Feed, Collection<Scan>>? = null) =
        CompletableFuture.supplyAsync {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            response.body!!.use {
                val stream = it.byteStream()
                val feedIo = FeedParser(stream, Xml.newPullParser())

                if(existingFeed != null) {
                    return@supplyAsync FeedUiContainer(existingFeed.first, feedIo, existingFeed.second)
                }

                return@supplyAsync FeedUiContainer(request.url.toUrl(), null, feedIo)
            }
        }
}