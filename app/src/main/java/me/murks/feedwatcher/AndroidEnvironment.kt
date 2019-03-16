package me.murks.feedwatcher

import android.content.Context
import me.murks.feedwatcher.data.DataStore

/**
 * @author zouroboros
 */
class AndroidEnvironment(context: Context): Environment {
    override val dataStore = DataStore(context)
    override val settings = AndroidSettings(context)
}