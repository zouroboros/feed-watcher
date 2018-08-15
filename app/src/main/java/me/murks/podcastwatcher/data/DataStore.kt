package me.murks.podcastwatcher.data

import me.murks.podcastwatcher.model.Feed
import me.murks.podcastwatcher.model.IFilter
import me.murks.podcastwatcher.model.Query
import java.net.URL
import java.util.*

/**
 * @author zouroboros
 * @date 8/13/18.
 */
class DataStore {
    fun getFeeds() : List<Feed> {
        return listOf(Feed(URL("https://feeds.br.de/hoerspiel-pool/feed.xml"), Date()),
                Feed(URL("https://www.ndr.de/kultur/radiokunst/podcast4336.xml"), Date()))
    }

    fun addFeed(feed: Feed) {
    }

    fun deleteFeed(feed: Feed) {

    }

    fun getQueries() : List<Query> {
        return emptyList()
    }

    fun addQuery(baseFilter: IFilter) : Query {
        return Query(-1, baseFilter)
    }

    fun deleteQuery(query: Query) {

    }
}