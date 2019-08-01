package me.murks.feedwatcher.model

/**
 * @author zouroboros
 * @date 8/17/18.
 */
data class Query(val id: Long, val name: String, val filter: List<Filter>)