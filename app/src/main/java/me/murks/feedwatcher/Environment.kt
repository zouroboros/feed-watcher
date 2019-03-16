package me.murks.feedwatcher

import me.murks.feedwatcher.data.DataStore

/**
 * @author zouroboros
 */
interface Environment {
    val dataStore: DataStore
    val settings: Settings
    val jobs: Jobs
}