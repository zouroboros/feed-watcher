package me.murks.podcastwatcher

import java.util.*

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
}