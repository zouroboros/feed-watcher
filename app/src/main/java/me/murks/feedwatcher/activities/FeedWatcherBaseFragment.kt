package me.murks.feedwatcher.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import me.murks.feedwatcher.AndroidEnvironment
import me.murks.feedwatcher.FeedWatcherApp

/**
 * @author zouroboros
 */
abstract class FeedWatcherBaseFragment(): androidx.fragment.app.Fragment() {
    lateinit var app: FeedWatcherApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = FeedWatcherApp(AndroidEnvironment(context!!))
    }

    override fun onDestroy() {
        super.onDestroy()
        app.close()
    }
}