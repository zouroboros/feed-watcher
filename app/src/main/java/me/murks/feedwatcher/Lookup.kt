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
Copyright 2021 Zouroboros
 */
package me.murks.feedwatcher

import java.util.*
import kotlin.collections.HashMap

/**
 * Associates a key with a collection of values.
 *
 * @author zouroboros
 */
class Lookup<K, V>(private val map: MutableMap<K, MutableList<V>>): Iterable<Map.Entry<K, Collection<V>>> {
    fun append(key: K, value: V) {
        if(!map.containsKey(key)) {
            map.put(key, LinkedList())
        }
        map.get(key)!!.add(value)
    }

    fun values(key: K) = map.get(key)

    fun flatList(): Collection<Map.Entry<K, V>> = map.entries.map {
        it.value.map { value -> AbstractMap.SimpleEntry(it.key, value) } }.flatten()

    override fun iterator(): Iterator<Map.Entry<K, Collection<V>>> = map.asIterable().iterator()
}

fun <K, V>Map<K, Collection<V>>.toLookup(): Lookup<K, V> {
    return Lookup<K, V>(HashMap(this.mapValues { LinkedList(it.value) }))
}

fun <K, V, T>Collection<T>.toLookup(key: (T) -> K, value: (T) -> V): Lookup<K, V> {
    return this.groupBy(key, value).toLookup()
}