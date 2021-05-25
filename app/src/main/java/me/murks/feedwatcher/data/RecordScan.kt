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
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.data

import me.murks.feedwatcher.model.*

import java.util.*

/**
 * Unit of work to insert the results and mark all feeds as updated.
 * @author zouroboros
 */
class RecordScan(private val results: List<Result>, private val scans: List<Scan>, private val updateDate: Date) : UnitOfWork {
    /**
     * Maximum number of scans that is kept for each feed.
     */
    private val MaxScansForFeed = 3

    override fun execute(store: DataStore) {
        val feeds = store.getFeeds()
        val newFeeds = feeds.map { Feed(it.url, updateDate, it.name) }

        store.startTransaction()

        for (result in results) {
            store.addResult(result);
        }

        for (feed in newFeeds) {
            store.updateFeed(feed)
        }

        for (scan in scans) {
            store.addScan(scan)
        }

        for (feed in feeds) {
            val scans = store.getScansForFeed(feed)
            if (scans.size > MaxScansForFeed) {
                val numberOfScansToDelete = scans.size - MaxScansForFeed
                val scansToDelete = scans
                    .sortedBy { it.scanDate }
                    .take(numberOfScansToDelete)
                for (scan in scansToDelete) {
                    store.deleteScan(scan)
                }
            }
        }
        store.commitTransaction()
    }
}