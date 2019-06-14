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
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.activities


import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import me.murks.feedwatcher.AndroidEnvironment
import me.murks.feedwatcher.Constants
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.R

class PreferencesFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var app: FeedWatcherApp

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        app = FeedWatcherApp(AndroidEnvironment(context!!))

        val scanInterval = findPreference<SeekBarPreference>(Constants.scanIntervalPreferencesKey)!!
        scanInterval.onPreferenceChangeListener = this
        scanInterval.min = 1
        val summary = scanInterval.summary.toString()
        scanInterval.summaryProvider = Preference.SummaryProvider<SeekBarPreference> {
            String.format(summary, it.value)
        }

    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        if(preference is SeekBarPreference) {
            // the SeekBarPreference is not updating its summary by itself -> we need to do it manually
            preference.value = newValue as Int
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            Constants.scanIntervalPreferencesKey -> app.rescheduleJobs()
            Constants.backgroundScanningPreferencesKey -> app.rescheduleJobs()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        app.environment.close()
    }
}
