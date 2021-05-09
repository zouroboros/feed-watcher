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
along with FeedWatcher. If not, see <https://www.gnu.org/licenses/>.
Copyright 2020 Zouroboros
 */
package me.murks.feedwatcher

import android.util.Log
import java.lang.Exception

/**
 * Wrapper for the android log functions that automatically generates tag based on the calling class and method.
 * @author zouroboros
 */
class FeedwatcherLog {
    fun info(message: String) {
        Log.v(tag(), message)
    }

    fun error(message: String) {
        Log.e(tag(), message)
    }

    fun error(message: String, exception: Throwable) {
        Log.e(tag(), message, exception)
    }

    private fun tag(): String  {
        val caller = Thread.currentThread().stackTrace.first { it.className.startsWith("me.murks.feedwatcher") && it.className != javaClass.name }
        return "${caller.className}.${caller.methodName}"
    }
}