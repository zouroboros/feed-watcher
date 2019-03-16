package me.murks.feedwatcher

import android.content.Context
import android.preference.PreferenceManager

class AndroidSettings(val context: Context): Settings {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override val showNotifcations: Boolean
        get() = preferences.getBoolean(context.getString(R.string.preferences_notifications_key), true)
    override val backgroundScanning: Boolean
        get() = preferences.getBoolean(context.getString(R.string.preferences_background_scanning_key), true)
    override val backgroundScanInterval: Int
        get() = preferences.getInt(context.getString(R.string.preferences_background_scanning_interval_key), 3)
}