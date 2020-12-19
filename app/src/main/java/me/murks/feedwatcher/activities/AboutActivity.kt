package me.murks.feedwatcher.activities

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.*
import me.murks.feedwatcher.R

class AboutActivity : FeedWatcherBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        about_web_view.loadUrl("file:///android_asset/about/about.html")
    }
}