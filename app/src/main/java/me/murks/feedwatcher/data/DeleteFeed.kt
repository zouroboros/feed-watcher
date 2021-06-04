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
Copyright 2021 Zouroboros
 */
package me.murks.feedwatcher.data

import me.murks.feedwatcher.model.Feed

/**
 * Implements the logic for deleting feeds.
 *
 * @author zouroboros
 */
class DeleteFeed(val feed: Feed): UnitOfWork {
    override fun execute(store: DataStore) {
        store.startTransaction()
        val results = store.getResultsForFeed(feed)
        val scans = store.getScansForFeed(feed)

        if (results.isEmpty()) {
            store.delete(feed)
        } else {
            store.markDeleted(feed)
        }

        for (scan in scans) {
            store.delete(scan)
        }

        store.commitTransaction()
    }
}