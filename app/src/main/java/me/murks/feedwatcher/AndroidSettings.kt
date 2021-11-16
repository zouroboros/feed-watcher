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

import android.content.Context
import androidx.preference.PreferenceManager

class AndroidSettings(val context: Context): Settings {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        // perform migrations
        if (!preferences.contains(Constants.scanIntervalTableIdPreferencesKey)) {
            preferences.edit().putInt(Constants.scanIntervalTableIdPreferencesKey, 0)
            preferences.edit().putInt(Constants.scanIntervalIdPreferencesKey,
                preferences.getInt(Constants.scanIntervalPreferencesKey, 3) - 1)
            preferences.edit().remove(Constants.scanIntervalPreferencesKey)
        }
    }

    override val showNotifications: Boolean
        get() = preferences.getBoolean(Constants.notificationsPreferencesKey, true)
    override val backgroundScanning: Boolean
        get() = preferences.getBoolean(Constants.backgroundScanningPreferencesKey, true)
    override val scanIntervalTableId: Int
        get() = preferences.getInt(Constants.scanIntervalTableIdPreferencesKey, 0)
    override val scanIntervalId: Int
        get() = preferences.getInt(Constants.scanIntervalIdPreferencesKey, 2)
}