package me.murks.feedwatcher.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import me.murks.feedwatcher.Lookup
import me.murks.feedwatcher.model.*
import me.murks.feedwatcher.using
import java.lang.IllegalStateException
import java.net.URL
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 * @author zouroboros
 */
class DataStore(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val writeDb: SQLiteDatabase = writableDatabase

    private val readDb: SQLiteDatabase = readableDatabase

    init {
        writeDb.setForeignKeyConstraintsEnabled(true)
    }


    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    override fun onCreate(db: SQLiteDatabase) {
        val feedsTable = "create table $FEEDS_TABLE ($ID integer primary key, " +
                "$FEED_URL text not null, $FEED_LAST_UPDATED text null, $FEED_DELETED boolean, " +
                "$FEED_NAME text not null)"
        val queryTable = "create table $QUERIES_TABLE ($ID integer primary key, " +
                "$QUERY_NAME text, $QUERY_DELETED boolean)"
        val filterTable = "create table $FILTER_TABLE ($ID integer primary key, $FILTER_TYPE text, " +
                "$FILTER_QUERY_ID integer, $FILTER_INDEX integer, " +
                "foreign key ($FILTER_QUERY_ID) references $QUERIES_TABLE($ID))"
        val filterParameterTable = "create table $FILTER_PARAMETER_TABLE ($ID integer primary key, " +
                "$FILTER_PARAMETER_NAME text, $FILTER_PARAMETER_STRING_VALUE text null, " +
                "$FILTER_PARAMETER_FILTER_ID integer, " +
                "foreign key ($FILTER_PARAMETER_FILTER_ID) references $FILTER_TABLE($ID))"
        val resultTable = "create table $RESULTS_TABLE ($ID integer primary key, $RESULT_FEED_ID integer, " +
                "$RESULT_FEED_ITEM_DESCRIPTION text, $RESULT_FEED_ITEM_LINK text, " +
                "$RESULT_FEED_ITEM_TITLE text, $RESULT_FEED_ITEM_DATE integer, " +
                 "$RESULT_FOUND integer, " +
                "foreign key ($RESULT_FEED_ID) references $FEEDS_TABLE($ID))"
        val resultQueriesTable = "create table $RESULTS_QUERIES_TABLE ($ID integer primary key, " +
                "$RESULTS_QUERIES_RESULT_ID int not null," +
                "$RESULTS_QUERIES_QUERY_ID int not null," +
                "foreign key ($RESULTS_QUERIES_RESULT_ID) references $RESULTS_TABLE($ID)," +
                "foreign key ($RESULTS_QUERIES_QUERY_ID) references $QUERIES_TABLE($ID))"

        db.beginTransaction()
        db.execSQL(feedsTable)
        db.execSQL(queryTable)
        db.execSQL(filterTable)
        db.execSQL(filterParameterTable)
        db.execSQL(resultTable)
        db.execSQL(resultQueriesTable)
        db.setTransactionSuccessful()
        db.endTransaction()
    }


    fun getFeeds(): List<Feed> {
        val cursor = readDb.query(FEEDS_TABLE, null, "$FEED_DELETED = 0", null,
                null, null, null)
        val feeds = LinkedList<Feed>()
        while (cursor.moveToNext()) {
            feeds.add(feed(cursor))
        }
        return feeds
    }

    fun delete(feed: Feed) {
        readDb.rawQuery("select count(*) from $FEEDS_TABLE join $RESULTS_TABLE " +
                "on $FEEDS_TABLE.$ID = $RESULTS_TABLE.$RESULT_FEED_ID " +
                "where $FEEDS_TABLE.$FEED_URL = ?", arrayOf(feed.url.toString())).use {
            val results = it.count
            if (results > 0) {
                val values = ContentValues().apply {
                    put(FEED_DELETED, 1)
                }
                writeDb.update(FEEDS_TABLE, values, "$FEED_URL = ?",
                        arrayOf(feed.url.toString()))
            } else {
                writeDb.delete(FEEDS_TABLE, "$FEED_URL = ?", arrayOf(feed.url.toString()))
            }
        }
    }

    private fun feed(cursor: Cursor): Feed {
        val url = URL(cursor.getString(cursor.getColumnIndex(FEED_URL)))
        val lastUpdated = if (!cursor.isNull(cursor.getColumnIndex(FEED_LAST_UPDATED))) {
            Date(cursor.getLong(cursor.getColumnIndex(FEED_LAST_UPDATED)))
        } else { null }
        val name = cursor.getString(cursor.getColumnIndex(FEED_NAME))
        return Feed(url, lastUpdated, name)
    }


    fun getQueries(): List<Query> {
        val filterId = "filterId"
        val queryId = "queryId"
        val cursor = queriesQuery(queryId, filterId, null)

        return loadQueries(cursor, filterId, queryId)
    }

    fun query(id: Long): Query {
        val queryId = "queryId"
        val filterId = "filterId"
        val cursor = queriesQuery(queryId, filterId, "$queryId = ?", id.toString())
        return loadQueries(cursor, filterId, queryId).first()
    }

    private fun queriesQuery(queryId: String, filterId: String,
                             where: String?, vararg args: String): Cursor {

        val selection = if (where != null) "where $where" else ""

        return readDb.rawQuery("select $QUERIES_TABLE.$ID as $queryId, " +
                "$FILTER_TABLE.$ID as $filterId, $FILTER_PARAMETER_TABLE.$ID as parameterId, " +
                "* from $QUERIES_TABLE " +
                "join $FILTER_TABLE on $QUERIES_TABLE.$ID = $FILTER_TABLE.$FILTER_QUERY_ID " +
                "join $FILTER_PARAMETER_TABLE on $FILTER_TABLE.$ID " +
                "= $FILTER_PARAMETER_TABLE.$FILTER_PARAMETER_FILTER_ID " +
                selection, args)
    }

    private fun loadQueries(cursor: Cursor, filterId: String, queryId: String): List<Query> {
        val filterParameter = Lookup(HashMap<Int, MutableList<FilterParameter>>())

        while (cursor.moveToNext()) {
            filterParameter.append(cursor.getInt(cursor.getColumnIndex(FILTER_PARAMETER_FILTER_ID)),
                    filterParameter(cursor))
        }

        val filter = Lookup(HashMap<Long, MutableList<Filter>>())

        if (cursor.moveToFirst()) {
            do {
                val queryId = cursor.getLong(cursor.getColumnIndex(FILTER_QUERY_ID))
                val id = cursor.getInt(cursor.getColumnIndex(filterId))
                filter.append(queryId, filter(cursor, filterParameter, id))
            } while (cursor.moveToNext())
        }

        val queries = LinkedList<Query>()
        val loaded = HashSet<Long>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(queryId))
                val query = query(cursor, filter, id)
                if (!loaded.contains(query.id)) {
                    queries.add(query)
                    loaded.add(query.id)
                }
            } while (cursor.moveToNext())
        }

        return queries
    }

    private fun query(cursor: Cursor, filter: Lookup<Long, Filter>, id: Long): Query {
        val name = cursor.getString(cursor.getColumnIndex(QUERY_NAME))
        return Query(id, name, filter.values(id)!!)
    }

    private fun filterParameter(cursor: Cursor): FilterParameter {
        val name = cursor.getString(cursor.getColumnIndex(FILTER_PARAMETER_NAME))
        val stringValue = cursor.getString(cursor.getColumnIndex(FILTER_PARAMETER_STRING_VALUE))
        return FilterParameter(name, stringValue)
    }

    private fun filter(cursor: Cursor, parameter: Lookup<Int, FilterParameter>, id: Int): Filter {
        val type = FilterType.valueOf(cursor.getString(cursor.getColumnIndex(FILTER_TYPE)))
        val index = cursor.getInt(cursor.getColumnIndex(FILTER_INDEX))

        if(type == FilterType.CONTAINS) {
            val text = parameter.values(id)!!.find { it.name == CONTAINS_FILTER_TEXT }!!
            return ContainsFilter(index, text.stringValue)
        } else if (type == FilterType.FEED) {
            val url = URL(parameter.values(id)!!.find { it.name == FEED_FILTER_URL }!!.stringValue)
            return FeedFilter(index, url)
        }

        throw IllegalStateException()
    }

    fun updateQuery(query: Query): Query {
        writeDb.beginTransaction()
        deleteQueryFilters(query);
        addQueryFilters(query);
        writeDb.update(QUERIES_TABLE, queryValues(query), "$ID = ?",
                arrayOf(query.id.toString()))
        writeDb.setTransactionSuccessful()
        writeDb.endTransaction()
        return query
    }

    private fun deleteQueryFilters(query: Query) {
        writeDb.delete(FILTER_PARAMETER_TABLE, "$FILTER_PARAMETER_FILTER_ID in " +
                "(select $ID from $FILTER_TABLE where $FILTER_QUERY_ID = ${query.id})",
                null)
        writeDb.delete(FILTER_TABLE, "$FILTER_QUERY_ID = ${query.id}", null)
    }

    fun addQuery(query: Query): Query {
        writeDb.beginTransaction()
        val queryId = writeDb.insert(QUERIES_TABLE, null, queryValues(query))
        val newId = writeDb.insert(QUERIES_TABLE,null,
                queryValues(Query(queryId, query.name, query.filter)))
        val newQuery = Query(newId, query.name, query.filter)
        addQueryFilters(newQuery)
        writeDb.setTransactionSuccessful()
        writeDb.endTransaction()
        return newQuery
    }

    private fun addQueryFilters(query: Query) {
        for (filter in query.filter) {
            val filterId = writeDb.insert(FILTER_TABLE, null,
                    filterValues(filter, query))

            val parameter = filter.filterCallback(object : FilterTypeCallback<List<ContentValues>> {
                override fun filter(filter: ContainsFilter): List<ContentValues> {
                    return listOf(ContentValues().apply {
                        put(FILTER_PARAMETER_FILTER_ID, filterId)
                        put(FILTER_PARAMETER_NAME, CONTAINS_FILTER_TEXT)
                        put(FILTER_PARAMETER_STRING_VALUE, filter.text)
                    })
                }

                override fun filter(filter: FeedFilter): List<ContentValues> {
                    return listOf(ContentValues().apply {
                        put(FILTER_PARAMETER_FILTER_ID, filterId)
                        put(FILTER_PARAMETER_NAME, FEED_FILTER_URL)
                        put(FILTER_PARAMETER_STRING_VALUE, filter.feedUrl.toString())
                    })
                }
            })

            for (p in parameter) {
                writeDb.insert(FILTER_PARAMETER_TABLE, null, p)
            }
        }
    }

    private fun queryValues(query: Query): ContentValues {
        val values = ContentValues()
        values.put(QUERY_NAME, query.name)
        return values
    }

    private fun filterValues(filter: Filter, query: Query): ContentValues {
        return ContentValues().apply {
            put(FILTER_QUERY_ID, query.id)
            put(FILTER_INDEX, filter.index)
            put(FILTER_TYPE, filter.type.name)
        }
    }

    private fun parameterValues(parameter: FilterParameter, filterId: Long): ContentValues {
        return ContentValues().apply {
            put(FILTER_PARAMETER_FILTER_ID, filterId)
            put(FILTER_PARAMETER_NAME, parameter.name)
            put(FILTER_PARAMETER_STRING_VALUE, parameter.stringValue)
        }
    }

    /**
     * Adds a new feed to the database. In case the database contains an old feed with the same url
     * this feeds deletion mark is unmarked
     */
    fun addFeed(feed: Feed) {
        val containsUrl = DatabaseUtils.queryNumEntries(readDb, FEEDS_TABLE,
                "$FEED_URL = ?", arrayOf(feed.url.toString())) > 0
        if(containsUrl) {
            val values = feedValues(feed)
            writeDb.update(FEEDS_TABLE, values, "$FEED_URL = ?",
                    arrayOf(feed.url.toString()))
        } else {
            val values = feedValues(feed)
            writeDb.insert(FEEDS_TABLE, null, values)
        }
    }

    private fun feedValues(feed: Feed): ContentValues {
        return ContentValues().apply {
            put(FEED_URL, feed.url.toString())
            if (feed.lastUpdate != null) {
                put(FEED_LAST_UPDATED, feed.lastUpdate.time)
            }
            put(FEED_DELETED, 0)
            put(FEED_NAME, feed.name)
        }
    }

    private fun resultsQuery(where: String? = null, args: List<String> = emptyList()): Cursor {
        val selection = if(where != null) {
            "where $where"
        } else {""}

        return readDb.rawQuery("select $QUERIES_TABLE.$ID $RESULTS_QUERY_QUERY_ID, " +
                "$FILTER_TABLE.$ID $RESULTS_QUERY_FILTER_ID, $RESULTS_TABLE.$ID $RESULTS_QUERY_RESULT_ID, " +
                "$FEEDS_TABLE.$ID $RESULTS_QUERY_FEED_ID, * from $RESULTS_TABLE " +
                "join $FEEDS_TABLE on $FEEDS_TABLE.$ID = $RESULTS_TABLE.$RESULT_FEED_ID " +
                "join $RESULTS_QUERIES_TABLE on $RESULTS_QUERIES_TABLE.$RESULTS_QUERIES_RESULT_ID " +
                "= $RESULTS_TABLE.$ID " +
                "join $QUERIES_TABLE on $QUERIES_TABLE.$ID " +
                "= $RESULTS_QUERIES_TABLE.$RESULTS_QUERIES_QUERY_ID " +
                "join $FILTER_TABLE on $FILTER_TABLE.$FILTER_QUERY_ID = $QUERIES_TABLE.$ID " +
                "join $FILTER_PARAMETER_TABLE on " +
                "$FILTER_PARAMETER_TABLE.$FILTER_PARAMETER_FILTER_ID = $FILTER_TABLE.$ID " +
                selection +
                "order by $RESULTS_TABLE.$RESULT_FOUND desc",
                args.toTypedArray())
    }

    private fun results(cursor: Cursor) : List<Result> {
        val queriesById = loadQueries(cursor, RESULTS_QUERY_FILTER_ID, RESULTS_QUERY_QUERY_ID).associateBy { it.id }

        val feeds = HashMap<Long, Feed>()

        if (cursor.moveToFirst()) {
            do {
                val feedId = cursor.getLong(cursor.getColumnIndex(RESULTS_QUERY_FEED_ID))
                feeds[feedId] = feed(cursor)
            } while (cursor.moveToNext())
        }

        val queriesByResultId = HashMap<Long, MutableSet<Query>>()

        if (cursor.moveToFirst()) {
            do {

                val resultId = cursor.getLong(cursor.getColumnIndex(RESULTS_QUERY_RESULT_ID))
                val queryId = cursor.getLong(cursor.getColumnIndex(RESULTS_QUERY_QUERY_ID))
                if (!queriesByResultId.containsKey(resultId)) {
                    queriesByResultId[resultId] = HashSet()
                }
                queriesByResultId[resultId]!!.add(queriesById[queryId]!!)

            } while (cursor.moveToNext())
        }

        val results = LinkedList<Result>()
        val resultSet = HashSet<Long>()

        if (cursor.moveToFirst()) {
            do {
                val resultId = cursor.getLong(cursor.getColumnIndex(RESULTS_QUERY_RESULT_ID))
                if (!resultSet.contains(resultId)) {
                    val result = result(resultId, cursor, feeds, queriesByResultId)
                    resultSet.add(resultId)
                    results.add(result)
                }
            } while (cursor.moveToNext())
        }
        return results
    }

    fun result(id: Long): Result {
        return using {
            var cursor = resultsQuery("$RESULTS_QUERY_RESULT_ID = ?", listOf(id.toString())).track()
            results(cursor).first()
        }
    }

    fun getResults(): List<Result> {

        return using {
            val cursor = resultsQuery().track()
            results(cursor)
        }
    }

    private fun <TQ : Collection<Query>> result(id: Long, cursor: Cursor, feeds: Map<Long, Feed>,
                                                queries: Map<Long, TQ>): Result {
        val id = cursor.getLong(cursor.getColumnIndex(RESULTS_QUERY_RESULT_ID))
        val title = cursor.getString(cursor.getColumnIndex(RESULT_FEED_ITEM_TITLE))
        val desc = cursor.getString(cursor.getColumnIndex(RESULT_FEED_ITEM_DESCRIPTION))
        val linkStr = cursor.getString(cursor.getColumnIndex(RESULT_FEED_ITEM_LINK))
        val link = if (linkStr != null) URL(linkStr) else null
        val feedDate = Date(cursor.getLong(cursor.getColumnIndex(RESULT_FEED_ITEM_DATE)))
        val date = Date(cursor.getLong(cursor.getColumnIndex(RESULT_FOUND)))

        val feedId = cursor.getLong(cursor.getColumnIndex(RESULT_FEED_ID))

        return Result(id, feeds[feedId]!!, queries[id]!!,
                FeedItem(title, desc, link, feedDate), date)
    }

    fun delete(result: Result) {
        using {
            writeDb.beginTransaction()

            val queryIds = readDb.rawQuery("select $QUERIES_TABLE.$ID from $QUERIES_TABLE join " +
                    "$RESULTS_QUERIES_TABLE on $RESULTS_QUERIES_QUERY_ID = $QUERIES_TABLE.$ID join " +
                    "$RESULTS_TABLE on $RESULTS_QUERIES_TABLE.$RESULTS_QUERIES_RESULT_ID = " +
                    "$RESULTS_TABLE.$ID where $QUERIES_TABLE.$QUERY_DELETED = 1 and " +
                    "$RESULTS_TABLE.$ID = ?", arrayOf(result.id.toString()))
                    .track()
                    .getColumnValues(ID) { c, i -> c.getLong(i) }

            for (queryId in queryIds) {
                writeDb.delete(QUERIES_TABLE, "$ID = ?", arrayOf(queryId.toString()))
            }

            val feedIds = readDb.rawQuery("select $FEEDS_TABLE.$ID from $FEEDS_TABLE join " +
                    "$RESULTS_TABLE on $FEEDS_TABLE.$ID = $RESULTS_TABLE.$RESULT_FEED_ID where " +
                    "$FEEDS_TABLE.$FEED_DELETED = 1 and $RESULTS_TABLE.$ID = ?",
                    arrayOf(result.id.toString()))
                    .track()
                    .getColumnValues(ID) { c, i -> c.getLong(i) }

            for (feedId in feedIds) {
                writeDb.delete(FEEDS_TABLE, "$ID = ?", arrayOf(feedId.toString()))
            }

            writeDb.delete(RESULTS_QUERIES_TABLE, "$RESULTS_QUERIES_RESULT_ID = ?",
                    arrayOf(result.id.toString()))

            writeDb.delete(RESULTS_TABLE, "$ID = ?", arrayOf(result.id.toString()))

            writeDb.setTransactionSuccessful()
            writeDb.endTransaction()
        }


    }

    fun delete(query: Query) {
        using {
            val results = readDb.rawQuery("select count(*) from $QUERIES_TABLE join " +
                    "$RESULTS_QUERIES_TABLE on $QUERIES_TABLE.$ID = " +
                    "$RESULTS_QUERIES_TABLE.$RESULTS_QUERIES_QUERY_ID join $RESULTS_TABLE on " +
                    "$RESULTS_QUERIES_TABLE.$RESULTS_QUERIES_RESULT_ID = $RESULTS_TABLE.$ID " +
                    "where $QUERY_DELETED.$ID = ?", arrayOf(query.id.toString())).track().count
            if (results > 0) {
                val values = ContentValues().apply {
                    put(QUERY_DELETED, 1)
                }
                writeDb.update(QUERIES_TABLE, values, "$ID = ?", arrayOf(query.id.toString()))
            } else {
                writeDb.delete(QUERIES_TABLE, "$ID = ?", arrayOf(query.id.toString()))
            }
        }

    }

    fun updateFeed(feed: Feed) {
        writeDb.update(FEEDS_TABLE, feedValues(feed), "$FEED_URL = ?",
                arrayOf(feed.url.toString()))
    }

    private fun addResult(result: Result) {
        writeDb.beginTransaction()
        val id = writeDb.insert(RESULTS_TABLE, null, resultValues(result))
        for (resultQuery in resultQueryValues(result, id)) {
            writeDb.insert(RESULTS_QUERIES_TABLE, null, resultQuery)
        }
        writeDb.setTransactionSuccessful()
        writeDb.endTransaction()
    }

    private fun resultQueryValues(result: Result, id: Long): Collection<ContentValues> {
        return result.queries.map {
            ContentValues().apply {
                put(RESULTS_QUERIES_RESULT_ID, id)
                put(RESULTS_QUERIES_QUERY_ID, it.id)
            }
        }
    }

    fun addResultAndUpdateFeed(result: Result, feed: Feed) {
        writeDb.beginTransaction()
        addResult(result)
        updateFeed(feed)
        writeDb.setTransactionSuccessful()
        writeDb.endTransaction()
    }

    private fun getFeedIdByURL(url: URL): Long? {
        return using {
            val db = readableDatabase
            val cursor = db.query(FEEDS_TABLE, null, "$FEED_URL = ?", arrayOf(url.toString()),
                    null, null, null)
                    .track()

            if (cursor.moveToFirst()) {
                cursor.getLong(cursor.getColumnIndex(ID))
            } else {
                null
            }
        }

    }

    private fun resultValues(result: Result): ContentValues {
        val feedId = getFeedIdByURL(result.feed.url)
        return ContentValues().apply {
            put(RESULT_FEED_ID, feedId)
            put(RESULT_FOUND, result.found.time)
            put(RESULT_FEED_ITEM_TITLE, result.item.title)
            put(RESULT_FEED_ITEM_DESCRIPTION, result.item.description)
            put(RESULT_FEED_ITEM_LINK, result.item.link?.toString())
            put(RESULT_FEED_ITEM_DATE, result.item.date.time)
        }
    }

    companion object {
        private const val DATABASE_NAME = "feedwatcher.db"
        private const val DATABASE_VERSION = 1

        // Tables
        private const val FEEDS_TABLE = "feeds"
        private const val FILTER_TABLE = "filter"
        private const val FILTER_PARAMETER_TABLE = "filter_parameter"
        private const val QUERIES_TABLE = "queries"
        private const val RESULTS_TABLE = "results"
        private const val RESULTS_QUERIES_TABLE = "results_queries"

        //Columns
        private const val ID = "id"

        private const val FEED_URL = "url"
        private const val FEED_LAST_UPDATED = "last_updated"
        private const val FEED_DELETED = "deleted"
        private const val FEED_NAME = "name"

        private const val FILTER_TYPE = "type"
        private const val FILTER_INDEX = "position"
        private const val FILTER_QUERY_ID = "query_id"

        private const val FILTER_PARAMETER_NAME = "parameter_name"
        private const val FILTER_PARAMETER_STRING_VALUE = "string_value"
        private const val FILTER_PARAMETER_FILTER_ID = "filter_id"

        private const val QUERY_NAME = "name"
        private const val QUERY_DELETED = "deleted"

        private const val RESULT_FEED_ID = "feed_id"
        private const val RESULT_FEED_ITEM_TITLE = "feed_item_title"
        private const val RESULT_FEED_ITEM_DESCRIPTION = "feed_item_description"
        private const val RESULT_FEED_ITEM_LINK = "feed_item_url"
        private const val RESULT_FEED_ITEM_DATE = "feed_item_date"
        private const val RESULT_FOUND = "found"

        private const val RESULTS_QUERIES_RESULT_ID = "resultId"
        private const val RESULTS_QUERIES_QUERY_ID = "queryId"

        private const val CONTAINS_FILTER_TEXT = "text"

        private const val FEED_FILTER_URL = "feedUrl"

        // field names in queries
        private const val RESULTS_QUERY_FILTER_ID = "mfilterId"
        private const val RESULTS_QUERY_QUERY_ID = "mqueryId"
        private const val RESULTS_QUERY_RESULT_ID = "mresultId"
        private const val RESULTS_QUERY_FEED_ID = "mfeedId"
    }
}