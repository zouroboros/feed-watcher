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
package me.murks.feedwatcher.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import me.murks.feedwatcher.Lookup
import me.murks.feedwatcher.model.*
import me.murks.feedwatcher.using
import me.murks.sqlschemaspec.ColumnSpec
import java.io.FileInputStream
import java.io.OutputStream
import java.net.URL
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 * @author zouroboros
 */
@SuppressLint("Range")
class DataStore(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // TODO move complex queries to UnitOfWork pattern

    companion object {
        private const val DATABASE_NAME = "feedwatcher.db"
        private const val DATABASE_VERSION = 4

        // Prefixes
        private const val FEEDS = "feeds"
        private const val QUERY = "query"
        private const val FILTER = "filter"
        private const val FILTER_PARAMETER = "filterParameter"
        private const val RESULTS = "result"
    }

    private val schema = FeedWatcherSchema()

    private val writeDb: SQLiteDatabase = writableDatabase

    private val readDb: SQLiteDatabase = readableDatabase

    init {
        writeDb.setForeignKeyConstraintsEnabled(true)
    }


    override fun onUpgrade(db: SQLiteDatabase, currentDbVersion: Int, schemaVersion: Int) {
        var dbVersion = currentDbVersion
        if(dbVersion == 1 && schemaVersion > 1) {
            Log.d(javaClass.name, "upgrading db from ${currentDbVersion} to 2.")
            db.execSQL("alter table ${schema.filterParameters.sqlName()} " +
                    "add column ${schema.filterParameters.dateValue.sqlName(false)} integer null")
            dbVersion = 2
        }

        if (dbVersion == 2 && schemaVersion > 2) {
            Log.d(javaClass.name, "upgrading db from ${currentDbVersion} to 3.")
            db.execSQL("delete from ${schema.feeds.sqlName()} where ${schema.feeds.deleted.sqlName()} = 1 and " +
                    "${schema.feeds.id.sqlName()} not in (select ${schema.results.feedId.sqlName()} from ${schema.results.sqlName()})")
            dbVersion = 3
        }

        if(dbVersion == 3 && schemaVersion > 3) {
            Log.d(javaClass.name, "upgrading db from ${currentDbVersion} to 4.")
            val tempTable = "filterParameters_backup";
            db.execSQL("create table ${tempTable} (id integer not null primary key, " +
                    "name text not null, " +
                    "stringValue text null, " +
                    "filterId integer not null references filters(id), " +
                    "dateValue integer null)")
            db.execSQL("insert into ${tempTable} select * from ${schema.filterParameters.sqlName()}")
            db.execSQL("drop table ${schema.filterParameters.sqlName()}")
            db.execSQL(schema.filterParameters.createStatement())
            db.execSQL("insert into ${schema.filterParameters.sqlName()} select * from ${tempTable}")
            db.execSQL("drop table ${tempTable}")
            dbVersion = 4
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        schema.createSchema(db)
    }


    fun getFeeds(): List<Feed> {
        val cursor = readDb.query(schema.feeds.getName(), null,
                "${schema.feeds.deleted.sqlName()} = 0", null,
                null, null, null)
        val feeds = LinkedList<Feed>()
        cursor.use {
            while (it.moveToNext()) {
                feeds.add(feed(cursor))
            }
        }

        return feeds
    }

    fun delete(feed: Feed) {
        val results = readDb.rawQuery("select count(*) from ${schema.feeds.sqlName()} " +
                "join ${schema.feeds.join(schema.results)} " +
                "where ${schema.feeds.url.sqlName()} = ?", arrayOf(feed.url.toString())).selectCount()
        if (results > 0) {
            val values = ContentValues().apply {
                put(schema.feeds.deleted.name, 1)
            }
            writeDb.update(schema.feeds.getName(), values,
                    "${schema.feeds.url.sqlName()} = ?",
                    arrayOf(feed.url.toString()))
        } else {
            writeDb.delete(schema.feeds.getName(),
                    "${schema.feeds.url.sqlName()} = ?", arrayOf(feed.url.toString()))
        }
    }

    private fun feed(cursor: Cursor, prefix: String = ""): Feed {
        val url = URL(getString(cursor, schema.feeds.url, prefix))
        val lastUpdated = if (!cursor.isNull(
                        cursor.getColumnIndex(prefix + schema.feeds.lastUpdated.name))) {
            Date(getLong(cursor, schema.feeds.lastUpdated, prefix)!!)
        } else { null }
        val name = getString(cursor, schema.feeds.name, prefix)!!
        return Feed(url, lastUpdated, name)
    }


    fun getQueries(): List<Query> {
        queriesQuery("${schema.queries.deleted.sqlName()} = 0").use {
            return loadQueries(it)
        }
    }

    fun query(id: Long): Query {
        queriesQuery("${schema.queries.id.sqlName()} = ?", id.toString()).use {
            return loadQueries(it).first()
        }
    }

    private fun queriesQuery(where: String?, vararg args: String): Cursor {

        val selection = if (where != null) "where $where" else ""

        return readDb.rawQuery("select ${schema.queries.prefixedColumns(QUERY)}, " +
                "${schema.filters.prefixedColumns(FILTER)}, " +
                "${schema.filterParameters.prefixedColumns(FILTER_PARAMETER)} " +
                "from ${schema.queries.sqlName()} " +
                "join ${schema.queries.join(schema.filters)} " +
                "join ${schema.filters.join(schema.filterParameters)} " +
                selection, args)
    }

    private fun getLong(cursor: Cursor, column: ColumnSpec, prefix: String): Long? {
        return if (!cursor.isNull(cursor.getColumnIndex(prefix + column.name))) {
            cursor.getLong(cursor.getColumnIndex(prefix + column.name))
        } else {
            null
        }
    }
    private fun getInt(cursor: Cursor, column: ColumnSpec, prefix: String): Int? {
        return if (!cursor.isNull(cursor.getColumnIndex(prefix + column.name))) {
            cursor.getInt(cursor.getColumnIndex(prefix + column.name))
        } else {
            null
        }
    }

    private fun getString(cursor: Cursor, column: ColumnSpec, prefix: String): String? {
        return if(!cursor.isNull(cursor.getColumnIndex(prefix + column.name))) {
            cursor.getString(cursor.getColumnIndex(prefix + column.name))
        } else {
            null
        }
    }

    private fun filterParameter(cursor: Cursor, prefix: String): FilterParameter {
        val name = getString(cursor, schema.filterParameters.name, prefix)!!
        val stringValue = getString(cursor, schema.filterParameters.stringValue, prefix)
        val dateValue = getLong(cursor, schema.filterParameters.dateValue, prefix)
        return FilterParameter(name, stringValue, if (dateValue != null) Date(dateValue) else null)
    }

    private fun filter(cursor: Cursor, prefix: String, parameter: Lookup<Long, FilterParameter>): Filter {
        val id = getLong(cursor, schema.filters.id, prefix)!!
        val type = FilterType.valueOf(getString(cursor, schema.filters.type, prefix)!!)
        val index = getInt(cursor, schema.filters.index, prefix)!!
        val parameters = parameter.values(id)!!

        return FilterFactory.new(index, type, parameters)
    }

    private fun query(cursor: Cursor, prefix: String, filter: Lookup<Long, Filter>): Query {
        val id = getLong(cursor, schema.queries.id, prefix)!!
        val name = getString(cursor, schema.queries.name, prefix)!!
        return Query(id, name, filter.values(id)!!)
    }

    private fun loadQueries(cursor: Cursor): List<Query> {
        val filterParameter = Lookup(HashMap<Long, MutableList<FilterParameter>>())

        while (cursor.moveToNext()) {
            filterParameter.append(getLong(cursor, schema.filterParameters.filterId, FILTER_PARAMETER)!!,
                    filterParameter(cursor, FILTER_PARAMETER))
        }

        val filter = Lookup(HashMap<Long, MutableList<Filter>>())

        if (cursor.moveToFirst()) {
            do {
                val queryId = getLong(cursor, schema.filters.queryId, FILTER)!!
                filter.append(queryId, filter(cursor, FILTER, filterParameter))
            } while (cursor.moveToNext())
        }

        val queries = LinkedList<Query>()
        val loaded = HashSet<Long>()

        if (cursor.moveToFirst()) {
            do {
                val query = query(cursor, QUERY, filter)
                if (!loaded.contains(query.id)) {
                    queries.add(query)
                    loaded.add(query.id)
                }
            } while (cursor.moveToNext())
        }

        return queries
    }

    fun updateQuery(query: Query): Query {
        writeDb.beginTransaction()
        deleteQueryFilters(query.id)
        addQueryFilters(query)
        writeDb.update(schema.queries.getName(), queryValues(query),
                "${schema.queries.id.sqlName()} = ?",
                arrayOf(query.id.toString()))
        writeDb.setTransactionSuccessful()
        writeDb.endTransaction()
        return query
    }

    private fun deleteQueryFilters(id: Long) {
        writeDb.delete(schema.filterParameters.sqlName(),
                "${schema.filterParameters.filterId.sqlName()} in " +
                "(select ${schema.filters.id.sqlName()} from ${schema.filters.sqlName()} " +
                        "where ${schema.filters.queryId.sqlName()} = ?)",
                arrayOf(id.toString()))
        writeDb.delete(schema.filters.sqlName(), "${schema.filters.queryId.sqlName()} = ?",
                arrayOf(id.toString()))
    }

    fun addQuery(query: Query): Query {
        writeDb.beginTransaction()
        val queryId = writeDb.insert(schema.queries.sqlName(), null, queryValues(query))
        val newQuery = Query(queryId, query.name, query.filter)
        addQueryFilters(newQuery)
        writeDb.setTransactionSuccessful()
        writeDb.endTransaction()
        return newQuery
    }

    private fun addQueryFilters(query: Query) {
        for (filter in query.filter) {
            val filterId = writeDb.insert(schema.filters.sqlName(), null,
                    filterValues(filter, query))

            val parameter = filter.parameter().map { ContentValues().apply {
                put(schema.filterParameters.filterId.name, filterId)
                put(schema.filterParameters.name.name, it.name)
                put(schema.filterParameters.stringValue.name, it.stringValue)
                put(schema.filterParameters.dateValue.name, it.dateValue?.time)
            } }

            for (p in parameter) {
                writeDb.insert(schema.filterParameters.getName(), null, p)
            }
        }
    }

    private fun queryValues(query: Query): ContentValues {
        val values = ContentValues()
        values.put(schema.queries.name.name, query.name)
        values.put(schema.queries.deleted.name, false)
        return values
    }

    private fun filterValues(filter: Filter, query: Query): ContentValues {
        return ContentValues().apply {
            put(schema.filters.queryId.sqlName(false), query.id)
            put(schema.filters.index.sqlName(false), filter.index)
            put(schema.filters.type.sqlName(false), filter.type.name)
        }
    }

    /**
     * Adds a new feed to the database. In case the database contains an old feed with the same url
     * this feeds deletion mark is unmarked
     */
    fun addFeed(feed: Feed) {
        val containsUrl = DatabaseUtils.queryNumEntries(readDb, schema.feeds.getName(),
                "${schema.feeds.url.sqlName()} = ?", arrayOf(feed.url.toString())) > 0
        if(containsUrl) {
            val values = feedValues(feed)
            writeDb.update(schema.feeds.getName(), values,
                    "${schema.feeds.url.sqlName()} = ?",
                    arrayOf(feed.url.toString()))
        } else {
            val values = feedValues(feed)
            writeDb.insert(schema.feeds.getName(), null, values)
        }
    }

    private fun feedValues(feed: Feed): ContentValues {
        return ContentValues().apply {
            put(schema.feeds.url.name, feed.url.toString())
            if (feed.lastUpdate != null) {
                put(schema.feeds.lastUpdated.name, feed.lastUpdate.time)
            }
            put(schema.feeds.deleted.name, 0)
            put(schema.feeds.name.name, feed.name)
        }
    }

    private fun resultsQuery(where: String? = null, args: List<String> = emptyList()): Cursor {
        val selection = if(where != null) {
            "where $where"
        } else {""}

        return readDb.rawQuery("select ${schema.queries.prefixedColumns(QUERY)}, " +
                "${schema.filters.prefixedColumns(FILTER)}, " +
                "${schema.filterParameters.prefixedColumns(FILTER_PARAMETER)}, " +
                "${schema.results.prefixedColumns(RESULTS)}, " +
                "${schema.feeds.prefixedColumns(FEEDS)} " +
                "from ${schema.results.sqlName()} " +
                "join ${schema.results.join(schema.feeds)} " +
                "join ${schema.results.join(schema.resultQueries)} " +
                "join ${schema.resultQueries.join(schema.queries)} " +
                "join ${schema.queries.join(schema.filters)} " +
                "join ${schema.filters.join(schema.filterParameters)} " +
                selection + " order by ${schema.results.found.sqlName()} desc",
                args.toTypedArray())
    }

    private fun results(cursor: Cursor) : List<Result> {
        val queriesById = loadQueries(cursor).associateBy { it.id }

        val feeds = HashMap<Long, Feed>()

        if (cursor.moveToFirst()) {
            do {
                val feedId = getLong(cursor, schema.feeds.id, FEEDS)!!
                feeds[feedId] = feed(cursor, FEEDS)
            } while (cursor.moveToNext())
        }

        val queriesByResultId = HashMap<Long, MutableSet<Query>>()

        if (cursor.moveToFirst()) {
            do {
                val resultId = getLong(cursor, schema.results.id, RESULTS)!!
                val queryId = getLong(cursor, schema.queries.id, QUERY)!!
                if (!queriesByResultId.containsKey(resultId)) {
                    queriesByResultId[resultId] = HashSet()
                }
                queriesByResultId[resultId]!!.add(queriesById.getValue(queryId))

            } while (cursor.moveToNext())
        }

        val results = LinkedList<Result>()
        val resultSet = HashSet<Long>()

        if (cursor.moveToFirst()) {
            do {
                val resultId = getLong(cursor, schema.results.id, RESULTS)!!
                if (!resultSet.contains(resultId)) {
                    val result = result(cursor, RESULTS, feeds, queriesByResultId)
                    resultSet.add(resultId)
                    results.add(result)
                }
            } while (cursor.moveToNext())
        }
        return results
    }

    fun result(id: Long): Result {
        resultsQuery("${schema.results.id.prefix(RESULTS)} = ?",
                listOf(id.toString())).use {
            return results(it).first()
        }
    }

    fun getResults(): List<Result> {
        resultsQuery().use {
            return results(it)
        }
    }

    private fun <TQ : Collection<Query>> result(cursor: Cursor, prefix: String, feeds: Map<Long, Feed>,
                                                                           queries: Map<Long, TQ>): Result {
        val id = getLong(cursor, schema.results.id, prefix)!!
        val title = getString(cursor, schema.results.title, prefix)!!
        val desc = getString(cursor, schema.results.description, prefix)!!
        val linkStr = getString(cursor, schema.results.link, prefix)
        val link = if (linkStr != null) URL(linkStr) else null
        val feedDate = Date(getLong(cursor, schema.results.date, prefix)!!)
        val date = Date(getLong(cursor, schema.results.found, prefix)!!)

        val feedId = getLong(cursor, schema.results.feedId, prefix)!!

        return Result(id, feeds.getValue(feedId), queries.getValue(id),
                FeedItem(title, desc, link, feedDate), date)
    }

    fun delete(result: Result) {
        using {
            val queryIds = readDb.rawQuery("select ${schema.queries.id.sqlName()} " +
                    "from ${schema.queries.sqlName()} " +
                    "join ${schema.queries.join(schema.resultQueries)} " +
                    "join ${schema.resultQueries.join(schema.results)} " +
                    "where ${schema.queries.deleted.sqlName()} = 1 and " +
                    "${schema.results.id.sqlName()} = ?", arrayOf(result.id.toString()))
                    .track()
                    .getColumnValues(schema.queries.id.name) { c, i -> c.getLong(i) }

            val feedIds = readDb.rawQuery("select ${schema.feeds.id.sqlName()} " +
                    "from ${schema.feeds.sqlName()} " +
                    "join ${schema.feeds.join(schema.results)} " +
                    "where " +
                    "${schema.feeds.deleted.sqlName()} = 1 and ${schema.results.id.sqlName()} = ? " +
                    "and (select count(${schema.results.feedId.sqlName()}) " +
                        "from ${schema.results.sqlName()} " +
                        "where ${schema.results.feedId.sqlName()} = " +
                        " ${schema.feeds.id.sqlName(true)}) = 1",
                    arrayOf(result.id.toString()))
                    .track()
                    .getColumnValues(schema.feeds.id.name) { c, i -> c.getLong(i) }

            writeDb.delete(schema.resultQueries.name,
                    "${schema.resultQueries.resultId.sqlName()} = ?",
                    arrayOf(result.id.toString()))

            writeDb.delete(schema.results.name, "${schema.results.id.sqlName()} = ?",
                    arrayOf(result.id.toString()))

            for (queryId in queryIds) {
                deleteQueryAndFilters(queryId)
            }

            for (feedId in feedIds) {
                writeDb.delete(schema.feeds.getName(),
                        "${schema.feeds.id.sqlName()} = ?", arrayOf(feedId.toString()))
            }
        }
    }

    fun delete(query: Query) {
        val results = readDb.rawQuery("select count(*) from ${schema.queries.sqlName()} " +
                "join ${schema.queries.join(schema.resultQueries)} " +
                "join ${schema.resultQueries.join(schema.results)} " +
                "where ${schema.queries.id.sqlName()} = ?",
                arrayOf(query.id.toString())).selectCount()
        if (results > 0) {
            val values = ContentValues().apply {
                put(schema.queries.deleted.name, 1)
            }
            writeDb.update(schema.queries.getName(), values,
                    "${schema.queries.id.sqlName()} = ?",
                    arrayOf(query.id.toString()))
        } else {
            deleteQueryAndFilters(query.id)
        }
    }

    private fun deleteQueryAndFilters(id: Long) {
        deleteQueryFilters(id)
        writeDb.delete(schema.queries.getName(),
                "${schema.queries.id.sqlName()} = ?", arrayOf(id.toString()))
    }

    fun updateFeed(feed: Feed) {
        writeDb.update(schema.feeds.getName(), feedValues(feed),
                "${schema.feeds.url.sqlName()} = ?",
                arrayOf(feed.url.toString()))
    }

    fun addResult(result: Result) {
        writeDb.beginTransaction()
        val id = writeDb.insert(schema.results.name, null, resultValues(result))
        for (resultQuery in resultQueryValues(result, id)) {
            writeDb.insert(schema.resultQueries.name, null, resultQuery)
        }
        writeDb.setTransactionSuccessful()
        writeDb.endTransaction()
    }

    private fun resultQueryValues(result: Result, id: Long): Collection<ContentValues> {
        return result.queries.map {
            ContentValues().apply {
                put(schema.resultQueries.resultId.name, id)
                put(schema.resultQueries.queryId.name, it.id)
            }
        }
    }

    private fun getFeedIdByURL(url: URL): Long? {
        return using {
            val cursor = readDb.query(schema.feeds.getName(), null,
                    "${schema.feeds.url.sqlName()} = ?", arrayOf(url.toString()),
                    null, null, null)
                    .track()

            if (cursor.moveToFirst()) {
                getLong(cursor, schema.feeds.id, "")
            } else {
                null
            }
        }
    }

    private fun resultValues(result: Result): ContentValues {
        val feedId = getFeedIdByURL(result.feed.url)
        return ContentValues().apply {
            put(schema.results.feedId.name, feedId)
            put(schema.results.found.name, result.found.time)
            put(schema.results.title.name, result.item.title)
            put(schema.results.description.name, result.item.description)
            put(schema.results.link.name, result.item.link?.toString())
            put(schema.results.date.name, result.item.date.time)
        }
    }

    fun startTransaction() {
        writeDb.beginTransaction();
    }

    fun commitTransaction() {
        writeDb.setTransactionSuccessful();
        writeDb.endTransaction();
    }

    fun abortTransaction() {
        writeDb.endTransaction();
    }

    fun submit(workUnit: UnitOfWork) {
        workUnit.execute(this)
    }

    override fun close() {
        writeDb.close()
        readDb.close()
        super.close()
    }

    fun export(output: OutputStream) {
        FileInputStream(readDb.path).use { it.copyTo(output) }
    }
}