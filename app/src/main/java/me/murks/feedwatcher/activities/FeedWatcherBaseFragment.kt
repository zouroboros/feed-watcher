package me.murks.feedwatcher.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import me.murks.feedwatcher.FeedWatcherApp

/**
 * @author zouroboros
 */
abstract class FeedWatcherBaseFragment(): Fragment() {
    lateinit var app: FeedWatcherApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = FeedWatcherApp(context!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        app.close()
    }
}