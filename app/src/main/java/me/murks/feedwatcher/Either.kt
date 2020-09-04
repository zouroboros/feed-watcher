/*
This file is part of FeedWatcher.

FeedWatcher is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FeedWatcher is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FeedWatcher.  If not, see <https://www.gnu.org/licenses/>.
Copyright 2020 Zouroboros
 */
package me.murks.feedwatcher

/**
 * Classes representing a success value (Right) or a error value (Left)
 * @author zouroboros
 */
sealed class Either<TL, TR>(protected val leftValue: TL?, protected val rightValue: TR?) {
    fun isLeft() = leftValue != null
    fun isRight() = rightValue != null

    fun <TResult> either(leftMapper: (TL) -> TResult, rightMapper: (TR) -> TResult)
            = if (isLeft()) leftMapper(leftValue!!) else rightMapper(rightValue!!)
}

data class Left<TL, TR>(val value: TL) : Either<TL, TR>(value, null)

data class Right<TL, TR>(val value: TR) : Either<TL, TR>(null, value)