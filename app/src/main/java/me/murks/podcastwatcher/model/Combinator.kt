package me.murks.podcastwatcher.model

/**
 * @author zouroboros
 * @date 8/13/18.
 */
class Combinator(override val type: FilterTypes,
                 override val leftFilter : Filter,
                 override val rightFilter : Filter) : IFilter {

    override fun normalForm(): Boolean {
        return false
    }
}