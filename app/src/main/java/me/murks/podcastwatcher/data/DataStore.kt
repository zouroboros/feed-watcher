package me.murks.podcastwatcher.data

import me.murks.podcastwatcher.model.*
import java.net.URL
import java.util.*

/**
 * @author zouroboros
 */
class DataStore {
    fun getFeeds() : List<Feed> {
        return listOf(Feed(URL("https://feeds.br.de/hoerspiel-pool/feed.xml"), Date()),
                Feed(URL("https://www.ndr.de/kultur/radiokunst/podcast4336.xml"), Date()))
    }

    fun getQueries() : List<Query> {
        return listOf(Query(1, "Test", listOf(Filter(FilterType.CONTAINS,
                listOf(FilterParameter("text", "test"))),
                Filter(FilterType.CONTAINS,
                        listOf(FilterParameter("text", "test"))))))
    }

    fun updateQuery(query: Query) {
    }

    fun addQuery(query: Query) {
    }

    fun addFeed(feed: Feed) {
    }

    fun getResults(): List<Result> {
        val query = getQueries()[0]
        val feed = getFeeds()[0]
        return listOf(Result(feed, query, "Test result", "Test description", Date(), URL("https://ddg.gg"), "Test feed"))
    }
}