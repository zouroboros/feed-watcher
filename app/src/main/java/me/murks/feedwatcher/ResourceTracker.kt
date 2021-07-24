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
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher

import java.net.HttpURLConnection
import java.util.*

/**
 * Resource tracker for closeable resources based on a
 * [thread](https://discuss.kotlinlang.org/t/kotlin-needs-try-with-resources/214/20)
 * in the kotlin language forum.
 * @author zouroboros
 */
class ResourceTracker : AutoCloseable {
    private val resources = LinkedList<AutoCloseable>()
    private val connections = LinkedList<HttpURLConnection>()

    fun <T: AutoCloseable> T.track(): T {
        resources.add(this)
        return this
    }

    fun <T: HttpURLConnection> T.track(): HttpURLConnection {
        connections.add(this)
        return this
    }

    override fun close() {
        resources.forEach { it.close() }
        connections.forEach { it.inputStream.close(); it.disconnect()}
    }

}

/**
 * Using block in which resources can be tracked using the [ResourceTracker.track] function
 * @see [ResourceTracker.track]
 */
fun <R> using(block: ResourceTracker.() -> R): R {
    val holder = ResourceTracker()
    try {
        return holder.block()
    } finally {
        holder.close()
    }
}

