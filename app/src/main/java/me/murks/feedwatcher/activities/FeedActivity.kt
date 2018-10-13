package me.murks.feedwatcher.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.FeedUrlTask
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * Activity for subscribing to feeds
 * @see [Feed]
 * @author zouroboros
 */
class FeedActivity : FeedWatcherBaseActivity(), FeedUrlTask.FeedUrlTaskReceiver {

    private lateinit var urlInput: EditText
    private lateinit var feedTitle: TextView
    private lateinit var feedDescription: TextView
    private lateinit var feedIcon: ImageView
    private lateinit var actionButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView

    private var edit = false
    private var feed: Feed? = null
    private var task = FeedUrlTask(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        urlInput = findViewById(R.id.feed_feed_url)
        feedTitle = findViewById(R.id.feed_feed_name)
        feedDescription = findViewById(R.id.feed_feed_description)
        feedIcon = findViewById(R.id.feed_feed_icon)
        actionButton = findViewById(R.id.feed_subscribe_button)
        progressBar = findViewById(R.id.feed_loading_progress_bar)
        errorText = findViewById(R.id.feed_feed_error)

        deactivateProgressBar()
        hideError()

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
        actionButton.isEnabled = false
        actionButton.setOnClickListener {
            if(edit) {
                app.delete(feed!!)
            } else {
                app.addFeed(feed!!)
            }
            finish()
        }

        if(intent.data != null || intent.hasExtra(Intent.EXTRA_TEXT)) {
            val url = if(intent.data != null) URL(intent.data.toString())
                else URL(intent.getStringExtra(Intent.EXTRA_TEXT))
            if(url != null) {
                urlInput.text.append(url.toString())
                urlInput.isEnabled = false
                edit = app.feeds().asSequence().map { it.url }.contains(url)
            }
        }
    }

    private fun showFeedsDetails(feedContainer: FeedUiContainer, activateButton: Boolean) {
        feedTitle.visibility = View.VISIBLE
        feedTitle.text = feedContainer.name
        actionButton.isEnabled = activateButton
        if(feedContainer.icon != null) {
            feedIcon.visibility = View.VISIBLE
            feedIcon.setImageBitmap(feedContainer.icon)
        } else {
            feedIcon.visibility = View.GONE
        }
        feedDescription.text = feedContainer.description
    }

    private fun hideFeedDetails() {
        feedTitle.visibility = View.INVISIBLE
        actionButton.isEnabled = false
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
        val feedAlreadyExists = app.feeds().asSequence().map { it.url }.contains(feedContainer.url)
        hideError()
        if (feedAlreadyExists && !edit) {
            showError(resources.getString(R.string.feed_already_subscribed))
        } else if(feedAlreadyExists && edit) {
            actionButton.setText(R.string.feed_unsubscribe)
        }
        showFeedsDetails(feedContainer, !feedAlreadyExists || edit)
        deactivateProgressBar()
    }

    override fun feedFailed(e: Exception) {
        hideFeedDetails()
        showError(resources.getText(R.string.url_loading_failed))
        deactivateProgressBar()
    }

    private fun hideError() {
        errorText.visibility = View.INVISIBLE
    }

    private fun showError(message: CharSequence) {
        errorText.visibility = View.VISIBLE
        errorText.text = message
    }
}
