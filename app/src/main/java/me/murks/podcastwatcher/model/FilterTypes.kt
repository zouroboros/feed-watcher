package me.murks.podcastwatcher.model

/**
 * @author zouroboros
 * @date 8/13/18.
 */
enum class FilterTypes(type: String) {
    CONTAINS("contains"),
    AND("and"),
    OR("or")
}