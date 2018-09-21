package me.murks.feedwatcher.activities

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.FeedUrlTask
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class FeedActivity : FeedWatcherBaseActivity(), FeedUrlTask.FeedUrlTaskReceiver {

    private lateinit var urlInput: EditText
    private lateinit var feedTitle: TextView
    private lateinit var feedDescription: TextView
    private lateinit var feedIcon: ImageView
    private lateinit var subscribeButton: Button
    private lateinit var progressBar: ProgressBar
    private var feed: Feed? = null
    private var task = FeedUrlTask(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        urlInput = findViewById(R.id.feed_feed_url)
        feedTitle = findViewById(R.id.feed_feed_name)
        feedDescription = findViewById(R.id.feed_feed_description)
        feedIcon = findViewById(R.id.feed_feed_icon)
        subscribeButton = findViewById(R.id.feed_subscribe_button)
        progressBar = findViewById(R.id.feed_loading_progress_bar)

        deactivateProgressBar()

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
                    hideFeedDetails()
                    if(task.status == AsyncTask.Status.FINISHED
                            || task.status == AsyncTask.Status.RUNNING) {
                        task.cancel(true)
                        task = FeedUrlTask(outer)
                    }
                    task.execute(url)
                    activateProgressBar()
                } catch (e: MalformedURLException) {
                    deactivateProgressBar()
                }
            }
        })
        subscribeButton.isEnabled = false
        subscribeButton.setOnClickListener {
            app.addFeed(feed!!)
            finish()
        }
    }

    private fun showFeedsDetails(feedContainer: FeedUiContainer) {
        feedTitle.text = feedContainer.name
        subscribeButton.isEnabled = true
        if(feedContainer.icon != null) {
            feedIcon.visibility = View.VISIBLE
            feedIcon.setImageBitmap(feedContainer.icon)
        } else {
            feedIcon.visibility = View.GONE
        }
        feedDescription.text = feedContainer.description
    }

    private fun hideFeedDetails() {
        subscribeButton.isEnabled = false
        feedDescription.visibility = View.GONE
        feedIcon.visibility = View.GONE
    }

    private fun activateProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun deactivateProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun feedLoaded(feedContainer: FeedUiContainer) {
        feed = Feed(feedContainer.url, Date(0L))
        showFeedsDetails(feedContainer)
        deactivateProgressBar()
    }

    override fun feedFailed(e: Exception) {
        hideFeedDetails()
        feedTitle.text = resources.getText(R.string.url_loading_failed)
        deactivateProgressBar()
    }
}
