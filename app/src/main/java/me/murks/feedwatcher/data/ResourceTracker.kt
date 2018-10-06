package me.murks.feedwatcher.data

import java.util.*

/**
 * Resource tracker for closeable resources based on
 * {@see https://discuss.kotlinlang.org/t/kotlin-needs-try-with-resources/214/20}
 * @author zouroboros
 */
class ResourceTracker : AutoCloseable {
    private val resources = LinkedList<AutoCloseable>()

    fun <T : AutoCloseable> T.track(): T {
        resources.add(this)
        return this
    }

    override fun close() {
        resources.reversed().forEach { it.close() }
    }

}

/**
 * Using block in which resources can be tracked using the {@see ResourceTracker.track} function
 */
fun <R> using(block: ResourceTracker.() -> R): R {
    val holder = ResourceTracker()
    try {
        return holder.block()
    } finally {
        holder.close()
    }
}

