package me.murks.feedwatcher.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import me.murks.feedwatcher.R

/**
 * Starts the specified activity
 * @author zouroboros
 */
fun <T: Activity> Context.startActivity(activityClass: Class<T>) {
    val intent = Intent(this, activityClass)
    startActivity(intent)
}

/**
 * Opens the feeds list in the overview activity
 */
fun Context.openFeeds() {
    val intent = Intent(this, OverviewActivity::class.java)
    intent.putExtra(OverviewActivity.CURRENT_FRAGMENT, R.id.nav_feeds)
    startActivity(intent)
}