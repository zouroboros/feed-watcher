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
Copyright 2019-2021 Zouroboros
 */
package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import me.murks.feedwatcher.*
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.model.Query
import me.murks.feedwatcher.model.Result
import java.util.*

/**
 * Task for filtering feeds in background.
 * @author zouroboros
 */
class FilterFeedsTask(private val queries: List<Query>,
                      private val log: FeedwatcherLog,
                      private val listener: TaskListener<FilterResult, List<FilterResult>>)
    : AsyncTask<Feed, FilterResult, List<FilterResult>>() {

    override fun doInBackground(vararg feeds: Feed): List<FilterResult> {

        val allResults = LinkedList<Either<Pair<Feed, Exception>, Pair<Feed, Collection<Result>>>>()

        log.info("Filtering feeds.")
        val results = FeedsFilter.filterFeeds(feeds.asList(), queries)
        results.forEach { result ->
            when (result) {
                is Left -> {
                    val feed = result.value.first
                    val error = result.value.second
                    log.error("Error filtering feed $feed.", error)
                }
                is Right -> {
                    val feed = result.value.first
                    val items = result.value.second
                    log.info("Found ${items.size} new entries for feed $feed.")
                    allResults.addAll(results)
                }
            }
            publishProgress(result)
        }
        log.info("Filtering feeds finished.")
        return allResults
    }


    override fun onPostExecute(result: List<FilterResult>) {
        listener.onResult(result)
        super.onPostExecute(result)
    }

    override fun onProgressUpdate(vararg values: FilterResult) {
        for (result in values) {
            listener.onProgress(result)
        }
        super.onProgressUpdate(*values)
    }
}