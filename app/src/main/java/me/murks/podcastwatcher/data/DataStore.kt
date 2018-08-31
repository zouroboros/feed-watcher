package me.murks.podcastwatcher.data

import me.murks.podcastwatcher.model.*
import java.net.URL
import java.util.*

/**
 * @author zouroboros
 */
class DataStore {
    fun getFeeds() : List<Feed> {
        return listOf(Feed(URL("https://feeds.br.de/hoerspiel-pool/feed.xml"), Date(0L)),
                Feed(URL("https://www.ndr.de/kultur/radiokunst/podcast4336.xml"), Date(0L)))
    }

    fun getQueries() : List<Query> {
        return listOf(Query(1, "Test", listOf(Filter(FilterType.CONTAINS,
                        listOf(FilterParameter("text", "Versetzung"))))))
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
        return listOf(Result(feed, query, FeedItem("Test result", "Test description", URL("https://ddg.gg"), Date()), Date(), "Test feed"))
    }

    fun updateFeed(feed: Feed) {

    }
}