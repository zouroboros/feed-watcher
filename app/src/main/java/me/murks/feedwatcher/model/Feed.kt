package me.murks.feedwatcher.model

import java.net.URL
import java.util.*

/**
 * @author zouroboros
 * @date 8/13/18.
 */
data class Feed(val url: URL, val lastUpdate : Date?, val name: String)