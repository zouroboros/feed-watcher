package me.murks.feedwatcher

import java.util.*
import kotlin.collections.HashMap

/**
 * @author zouroboros
 */
class Lookup<K, V>(private val map: MutableMap<K, MutableList<V>>) {
    fun append(key: K, value: V) {
        if(!map.containsKey(key)) {
            map.put(key, LinkedList())
        }
        map.get(key)!!.add(value)
    }

    fun values(key: K) = map.get(key)

    fun flatList(): Collection<Map.Entry<K, V>> = map.entries.map {
        it.value.map { value -> AbstractMap.SimpleEntry(it.key, value) } }.flatten()
}

fun <K, V>Map<K, Collection<V>>.toLookup(): Lookup<K, V> {
    return Lookup<K, V>(HashMap(this.mapValues { LinkedList(it.value) }))
}

fun <K, V, T>Collection<T>.toLookup(key: (T) -> K, value: (T) -> V): Lookup<K, V> {
    return this.groupBy(key, value).toLookup()
}