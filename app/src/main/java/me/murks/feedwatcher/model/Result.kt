package me.murks.feedwatcher.model

import java.util.*

/**
 * @author zouroboros
 */
data class Result(val id: Long, val feed: Feed, val queries: Collection<Query>, val item: FeedItem,
                  val found: Date)