package me.murks.feedwatcher

import android.content.Context
import android.preference.PreferenceManager

class AndroidSettings(val context: Context): Settings {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override val showNotifcations: Boolean
        get() = preferences.getBoolean(Constants.notificationsPreferencesKey, true)
    override val backgroundScanning: Boolean
        get() = preferences.getBoolean(Constants.backgroundScanningPreferencesKey, true)
    override val backgroundScanInterval: Int
        get() = preferences.getInt(Constants.scanIntervalPreferencesKey, 3)
}