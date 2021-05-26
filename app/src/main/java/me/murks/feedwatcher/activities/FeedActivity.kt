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
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat

import me.murks.feedwatcher.databinding.ActivityFeedBinding
import me.murks.feedwatcher.R
import me.murks.feedwatcher.Texts
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.ErrorHandlingTaskListener
import me.murks.feedwatcher.tasks.LoadImageTask
import me.murks.feedwatcher.tasks.Tasks
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

/**
 * Activity for subscribing to feeds
 * @see [Feed]
 * @author zouroboros
 */
class FeedActivity : FeedWatcherBaseActivity(),
        ErrorHandlingTaskListener<Pair<URL, Bitmap>, Void, IOException> {

    private lateinit var binding: ActivityFeedBinding

    private var edit = false
    private var feed: Feed? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deactivateProgressBar()

        binding.feedFeedUrl.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                tryLoad(p0!!)
            }
        })
        binding.feedSubscribeButton.isEnabled = false
        binding.feedSubscribeButton.setOnClickListener {
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
                binding.feedFeedUrl.text.append(url)
                edit = app.feeds().asSequence().map { it.url.toString() }.contains(url)
                if (edit) {
                    binding.feedAddFeedLabel.text = resources.getString(R.string.add_feed_edit_feed_label)
                    binding.feedFeedUrl.isEnabled = false
                    binding.feedSubscribeButton.setText(R.string.feed_unsubscribe)
                }
                tryLoad(binding.feedFeedUrl.text)
            }
        }
    }

    private fun tryLoad(p0: Editable) {
        val urlText = p0.toString()
        try {
            binding.feedFeedScanInfo.text = ""
            val url = URL(urlText)
            hideFeedDetails()

            app.getFeedForUrl(url)
                .thenCompose { Tasks.loadFeedUiContainer(url, it) }
                .thenAcceptAsync( {
                    showFeedsDetails(it, it.feed == null && !edit)
                    deactivateProgressBar()
                }, ContextCompat.getMainExecutor(this)).exceptionally {
                    hideFeedDetails()
                    binding.feedFeedScanInfo.text = resources.getText(R.string.url_loading_failed)
                    app.environment.log.error("Loading feed failed.", it)
                    deactivateProgressBar()
                    // weird hack to get void value
                    null
                }

            activateProgressBar()
        } catch (e: MalformedURLException) {
            deactivateProgressBar()
            binding.feedFeedScanInfo.text = resources.getString(R.string.add_feed_invalid_url)
        }
    }

    private fun showFeedsDetails(feedContainer: FeedUiContainer, activateButton: Boolean) {
        binding.feedFeedName.visibility = View.VISIBLE
        binding.feedFeedName.text = feedContainer.name
        binding.feedSubscribeButton.isEnabled = activateButton
        if(feedContainer.icon != null) {
            binding.feedFeedIcon.visibility = View.VISIBLE
            LoadImageTask(this).execute(feedContainer.icon)
        } else {
            binding.feedFeedIcon.visibility = View.GONE
        }
        binding.feedFeedDescription.text = feedContainer.description ?: feedContainer.name

        if (feedContainer.scans.isEmpty()) {
            binding.feedFeedScanInfo.text = resources.getString(R.string.never_scanned)
        } else if (feedContainer.scans.all { it.sucessfully }) {
            val dateTime = Formatter.dateToString(this, feedContainer.scans.first().scanDate)
            binding.feedFeedScanInfo.text = resources.getString(R.string.last_scan, dateTime)
        } else if (feedContainer.scans.any { it.sucessfully }) {
            val lastSucess = feedContainer.scans.first { it.sucessfully }
            val lastFailure = feedContainer.scans.first { !it.sucessfully }

            if (lastSucess.scanDate.after(lastFailure.scanDate)) {
                val dateTime = Formatter.dateToString(this, lastSucess.scanDate)
                binding.feedFeedScanInfo.text = resources.getString(R.string.last_scan, dateTime)
            } else {
                val lastSucessDateTime = Formatter.dateToString(this, lastSucess.scanDate)
                val lastFailureDateTime = Formatter.dateToString(this, lastFailure.scanDate)
                binding.feedFeedScanInfo.text = resources.getString(R.string.last_scan_failed_last_success, lastFailureDateTime, lastSucessDateTime)
            }

        } else if (feedContainer.scans.all { !it.sucessfully }) {
            val dateTime = Formatter.dateToString(this, feedContainer.scans.first().scanDate)
            binding.feedFeedScanInfo.text = resources.getString(R.string.last_scan_failed, dateTime)
        } else {
            // just for debugging should never happen
            throw IllegalArgumentException()
        }
    }

    private fun hideFeedDetails() {
        binding.feedFeedName.visibility = View.INVISIBLE
        binding.feedSubscribeButton.isEnabled = edit
        binding.feedFeedDescription.visibility = View.GONE
        binding.feedFeedIcon.visibility = View.GONE
    }

    private fun activateProgressBar() {
        binding.feedLoadingProgressBar.visibility = View.VISIBLE
    }

    private fun deactivateProgressBar() {
        binding.feedLoadingProgressBar.visibility = View.INVISIBLE
    }

    override fun onSuccessResult(result: Void) {}

    override fun onErrorResult(error: IOException) {}

    override fun onProgress(progress: Pair<URL, Bitmap>) {
        binding.feedFeedIcon.setImageBitmap(progress.second)
    }
}
