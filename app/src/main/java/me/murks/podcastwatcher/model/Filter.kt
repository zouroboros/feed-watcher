package me.murks.podcastwatcher.model

/**
 * @author zouroboros
 * @date 8/13/18.
 */
class Filter(override val type: FilterTypes, val parameter: List<FilterParameter>) : IFilter {
    override val leftFilter: IFilter?
        get() = null
    override val rightFilter: IFilter?
        get() = null

    override fun normalForm(): Boolean {
        return true
    }
}