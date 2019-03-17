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
        app.close()
    }
}
