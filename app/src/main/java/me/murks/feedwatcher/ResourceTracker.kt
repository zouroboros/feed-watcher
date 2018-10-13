package me.murks.feedwatcher

import java.net.URLConnection
import java.util.*

/**
 * Resource tracker for closeable resources based on a
 * [thread](https://discuss.kotlinlang.org/t/kotlin-needs-try-with-resources/214/20)
 * in the kotlin language forum.
 * @author zouroboros
 */
class ResourceTracker : AutoCloseable {
    private val resources = LinkedList<AutoCloseable>()
    private val connections = LinkedList<URLConnection>()

    fun <T: AutoCloseable> T.track(): T {
        resources.add(this)
        return this
    }

    fun <T: URLConnection> T.track(): URLConnection {
        connections.add(this)
        return this
    }

    override fun close() {
        resources.reversed().forEach { it.close() }
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

