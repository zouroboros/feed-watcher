package me.murks.podcastwatcher

/**
 * @author zouroboros
 * @date 8/27/18.
 */
sealed class Either<TL, TR>() {
    abstract fun isLeft(): Boolean
    abstract fun isRight(): Boolean
}
class Left<TL, TR>(val value: TL): Either<TL, TR>() {
    override fun isLeft() = true
    override fun isRight() = false
}

class Right<TL, TR>(val value: TR): Either<TL, TR>() {
    override fun isLeft() = false
    override fun isRight() = true
}