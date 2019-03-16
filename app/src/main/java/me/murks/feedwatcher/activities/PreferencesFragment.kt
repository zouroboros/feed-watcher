package me.murks.feedwatcher.activities


import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.R

class PreferencesFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private val scanIntervalKey = "scan_interval"
    private val backgroundScanningKey = "scan_background_scan"
    private val notificationsKey = "new_results_notification"

    private lateinit var app: FeedWatcherApp

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        app = FeedWatcherApp(context!!)

        val scanInterval = findPreference<SeekBarPreference>(scanIntervalKey)!!
        scanInterval.onPreferenceChangeListener = this
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
            scanIntervalKey -> app.rescheduleScanner()
            backgroundScanningKey -> app.rescheduleScanner()
        }
    }
}
