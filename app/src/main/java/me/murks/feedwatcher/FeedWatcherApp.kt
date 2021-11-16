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
package me.murks.feedwatcher

import me.murks.feedwatcher.data.*
import me.murks.feedwatcher.model.*
import me.murks.feedwatcher.tasks.FilterResult
import me.murks.jopl.OpOutline
import me.murks.jopl.Outlines
import java.io.OutputStream
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author zouroboros
 */
class FeedWatcherApp(val environment: Environment) {
    // TODO every database access should use completable future

    private val intervalTables = arrayListOf(
        listOf(ScanInterval(0, 1), ScanInterval(0, 2),
            ScanInterval(0, 3), ScanInterval(0, 4),
            ScanInterval(0, 5), ScanInterval(0, 6),
            ScanInterval(0, 7), ScanInterval(0, 8),
            ScanInterval(0, 9), ScanInterval(0, 10),
            ScanInterval(0, 11), ScanInterval(0, 12),
            ScanInterval(0, 13), ScanInterval(0, 14),
            ScanInterval(0, 15), ScanInterval(0, 16),
            ScanInterval(0, 17), ScanInterval(0, 18),
            ScanInterval(0, 19), ScanInterval(0, 20),
            ScanInterval(0, 21), ScanInterval(0, 22),
            ScanInterval(0, 23), ScanInterval(0, 24)),
        listOf(ScanInterval(15, 0), ScanInterval(30, 0),
            ScanInterval(0, 1), ScanInterval(0, 2),
            ScanInterval(0, 3), ScanInterval(0, 4),
            ScanInterval(0, 5), ScanInterval(0, 6),
            ScanInterval(0, 7), ScanInterval(0, 8),
            ScanInterval(0, 9), ScanInterval(0, 10),
            ScanInterval(0, 11), ScanInterval(0, 12),
            ScanInterval(0, 13), ScanInterval(0, 14),
            ScanInterval(0, 15), ScanInterval(0, 16),
            ScanInterval(0, 17), ScanInterval(0, 18),
            ScanInterval(0, 19), ScanInterval(0, 20),
            ScanInterval(0, 21), ScanInterval(0, 22),
            ScanInterval(0, 23), ScanInterval(0, 24)))

    fun queries(): List<Query> {
        val queries = environment.dataStore.getQueries()
        return queries
    }

    fun feeds(): List<Feed> {
        val feeds = environment.dataStore.getFeeds()
        return feeds
    }

    fun getFeedsWithScans(): Lookup<Feed, Scan> = environment.dataStore.getFeedsWithScans()

    fun getFeedForUrl(url: URL): CompletableFuture<Pair<Feed, Collection<Scan>>> =
        CompletableFuture.supplyAsync { environment.dataStore.getFeedWithScans(url) }

    fun results()= environment.dataStore.getResults()

    fun updateQuery(query: Query) {
        environment.dataStore.updateQuery(query)
    }

    fun addQuery(query: Query) {
        environment.dataStore.addQuery(query)
    }

    fun addFeed(feed: Feed) {
        environment.dataStore.addFeed(feed)
    }

    fun delete(feed: Feed) {
        environment.dataStore.submit(DeleteFeed(feed))
    }

    fun query(id: Long): Query {
        return environment.dataStore.query(id)
    }

    fun result(id: Long): Result {
        return environment.dataStore.result(id)
    }

    fun delete(result: Result) {
        environment.dataStore.submit(DeleteResult(result))
    }

    fun delete(query: Query) {
        environment.dataStore.delete(query)
    }

    /**
     * Reschedules the background scanning according to the current settings
     */
    fun rescheduleJobs() {
        if (environment.settings.backgroundScanning) {
            val scanInterval = intervalTables[
                    environment.settings.scanIntervalTableId][environment.settings.scanIntervalId]
            environment.jobs.rescheduleJobs(scanInterval)

        } else {
            environment.jobs.removeSchedule()
        }
    }

    /**
     * Sends the scan results to the app
     */
    fun scanResults(scanResults: Sequence<FilterResult>) {
        val results = scanResults.flatMap { it.either({ emptyList() }, { pair -> pair.second }) }.toList()
        val scanDate = Date()
        val scanRecords = scanResults.map { it.either(
            { left -> Scan(left.first, false, left.second.localizedMessage, scanDate)},
            { right -> Scan(right.first, true, null, scanDate) }) }
            .toList()

        if(results.isNotEmpty() && environment.settings.showNotifications) {
            environment.notifications.newResults(results, environment.settings)
        }

        environment.dataStore.submit(RecordScan(results, scanRecords, scanDate))
    }

    /**
     * Function for batch importing feeds based on a list of outlines
     */
    fun import(outlines: Collection<OpOutline>) {
        environment.dataStore.submit(AddFeeds(outlines.map {
            outline -> Feed(URL(outline.xmlUrl), null, outline.title)
        }))
    }

    /**
     * Exports all feeds into an OPML Outline.
     */
    fun exportFeeds(exportName: String): Outlines {
        return Outlines(exportName, Date(),
                feeds().map { OpOutline(it.name, it.name, "rss", it.url.toString(), it.url.toString()) })
    }

    /**
     * Deletes all results.
     */
    fun deleteResults() {
        environment.dataStore.submit(ClearResults())
    }

    /**
     * Exports the database
     */
    fun exportDatabase(output: OutputStream) {
        environment.dataStore.export(output)
    }

    fun getCurrentScanIntervals() = intervalTables[environment.settings.scanIntervalTableId]

}