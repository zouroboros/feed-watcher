package me.murks.podcastwatcher.model

import java.net.URL
import java.util.*

/**
 * @author zouroboros
 */
data class Result(val feed: Feed, val query: Query, val name: String, val description: String,
                  val found: Date, val link: URL?, val feedName: String)