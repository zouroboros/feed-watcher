package me.murks.podcastwatcher.model

/**
 * @author zouroboros
 * @date 8/13/18.
 */
interface IFilter {
    fun normalForm(): Boolean
    val type: FilterTypes
    val leftFilter: IFilter?
    val rightFilter: IFilter?
}