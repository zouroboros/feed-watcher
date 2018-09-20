package me.murks.feedwatcher.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import me.murks.feedwatcher.FeedWatcherApp

/**
 * @author zouroboros
 */
abstract class FeedWatcherBaseActivity(): AppCompatActivity() {
    lateinit var app: FeedWatcherApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = FeedWatcherApp(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        app.close()
    }
}