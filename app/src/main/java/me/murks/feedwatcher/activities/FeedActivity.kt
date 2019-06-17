/*
This file is part of FeedWatcher.

FeedWatcher is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FeedWatcher is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FeedWatcher. If not, see <https://www.gnu.org/licenses/>.
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import me.murks.feedwatcher.R
import me.murks.feedwatcher.Texts
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.FeedUrlTask
import java.net.MalformedURLException
import java.net.URL

/**
 * Activity for subscribing to feeds
 * @see [Feed]
 * @author zouroboros
 */
class FeedActivity : FeedWatcherBaseActivity(), FeedUrlTask.FeedUrlTaskReceiver {

    private lateinit var label: TextView
    private lateinit var urlInput: EditText
    private lateinit var feedTitle: TextView
    private lateinit var feedDescription: TextView
    private lateinit var feedIcon: ImageView
    private lateinit var actionButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var lastChecked: TextView

    private var edit = false
    private var feed: Feed? = null
    private lateinit var task: FeedUrlTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        label = findViewById(R.id.feed_add_feed_label)
        urlInput = findViewById(R.id.feed_feed_url)
        feedTitle = findViewById(R.id.feed_feed_name)
        feedDescription = findViewById(R.id.feed_feed_description)
        feedIcon = findViewById(R.id.feed_feed_icon)
        actionButton = findViewById(R.id.feed_subscribe_button)
        progressBar = findViewById(R.id.feed_loading_progress_bar)
        errorText = findViewById(R.id.feed_feed_error)
        lastChecked = findViewById(R.id.feed_feed_last_checked)

        deactivateProgressBar()
        hideError()

        task = FeedUrlTask(this, app.feeds())

        urlInput.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                tryLoad(p0!!)
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
            val text = if(intent.data != null) intent.data.toString() else
                intent.getStringExtra(Intent.EXTRA_TEXT)
            if(text != null) {
                val url = Texts.findUrl(text)?.toString()?: text
                urlInput.text.append(url)
                edit = app.feeds().asSequence().map { it.url.toString() }.contains(url)
                if (edit) {
                    label.text = resources.getString(R.string.add_feed_edit_feed_label)
                }
                tryLoad(urlInput.text)
            }
        }
    }

    private fun tryLoad(p0: Editable) {
        val urlText = p0.toString()
        try {
            hideError()
            val url = URL(urlText)
            hideFeedDetails()
            if(task.status == AsyncTask.Status.FINISHED
                    || task.status == AsyncTask.Status.RUNNING) {
                task.cancel(true)
                task = FeedUrlTask(this, app.feeds())
            }
            task.execute(url)
            activateProgressBar()
        } catch (e: MalformedURLException) {
            deactivateProgressBar()
            showError(resources.getString(R.string.add_feed_invalid_url))
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
        if(feedContainer.updated != null) {
            lastChecked.text = DateFormat.getDateFormat(this).format(feedContainer.updated) +
                    " " + DateFormat.getTimeFormat(this).format(feedContainer.updated)
        } else {
            lastChecked.text = resources.getString(R.string.add_feed_never_updated);
        }
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
        feed = Feed(feedContainer.url, null, feedContainer.name)
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
        e.printStackTrace()
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
