package me.murks.podcastwatcher.activities

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import me.murks.podcastwatcher.PodcastWatcherApp
import me.murks.podcastwatcher.R
import me.murks.podcastwatcher.model.Feed
import me.murks.podcastwatcher.tasks.FeedUrlTask
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class FeedActivity : AppCompatActivity(), FeedUrlTask.FeedUrlTaskReceiver {

    private lateinit var urlInput: EditText
    private lateinit var feedTitle: TextView
    private lateinit var feedDescription: TextView
    private lateinit var feedIcon: ImageView
    private lateinit var subscribeButton: Button
    private lateinit var app: PodcastWatcherApp
    private var feed: Feed? = null
    private var task = FeedUrlTask(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        app = PodcastWatcherApp(this)

        urlInput = findViewById(R.id.feed_feed_url)
        feedTitle = findViewById(R.id.feed_feed_name)
        feedDescription = findViewById(R.id.feed_feed_description)
        feedIcon = findViewById(R.id.feed_feed_icon)
        subscribeButton = findViewById(R.id.feed_subscribe_button)

        val outer = this

        urlInput.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val urlText = p0.toString()
                try {
                    val url = URL(urlText)
                    if(task.status == AsyncTask.Status.FINISHED
                            || task.status == AsyncTask.Status.RUNNING) {
                        task.cancel(true)
                        task = FeedUrlTask(outer)
                    }
                    task.execute(url)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            }
        })
        subscribeButton.isEnabled = false
        subscribeButton.setOnClickListener {
            app.addFeed(feed!!)
            finish()
        }
    }

    override fun feedLoaded(feedContainer: FeedUiContainer) {
        feedTitle.text = feedContainer.name
        subscribeButton.isEnabled = true
        if(feedContainer.icon != null) {
            feedIcon.visibility = View.VISIBLE
            feedIcon.setImageBitmap(feedContainer.icon)
        } else {
            feedIcon.visibility = View.GONE
        }
        feedDescription.text = feedContainer.description
        feed = Feed(feedContainer.url, Date(0L))
    }

    override fun feedFailed(e: Exception) {
        e.printStackTrace()
        subscribeButton.isEnabled = false
        feedTitle.text = resources.getText(R.string.url_loading_failed)
    }
}
