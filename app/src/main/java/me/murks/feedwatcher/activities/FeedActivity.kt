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
Copyright 2019 - 2021 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat

import me.murks.feedwatcher.databinding.ActivityFeedBinding
import me.murks.feedwatcher.R
import me.murks.feedwatcher.Texts
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.Tasks
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.CompletableFuture

/**
 * Activity for subscribing to feeds
 * @see [Feed]
 * @author zouroboros
 */
class FeedActivity : FeedWatcherBaseActivity() {

    private lateinit var binding: ActivityFeedBinding

    private var feedContainer: FeedUiContainer? = null
    private var feedFuture: CompletableFuture<Void>? = null

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
            if (feedContainer != null) {
                if(feedContainer!!.feed != null) {
                    app.delete(feedContainer!!.feed!!)
                } else {
                    val feed = Feed(feedContainer!!.url, null, feedContainer!!.name)
                    app.addFeed(feed)
                }
                finish()
            }
        }

        if(intent.data != null || intent.hasExtra(Intent.EXTRA_TEXT)) {
            val text = if(intent.data != null) intent.data.toString() else
                intent.getStringExtra(Intent.EXTRA_TEXT)
            if(text != null) {
                val url = Texts.findUrl(text)?.toString()?: text
                binding.feedFeedUrl.text.append(url)
                val edit = app.feeds().asSequence().map { it.url.toString() }.contains(url)
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

            feedFuture?.cancel(true)

            feedFuture = app.getFeedForUrl(url)
                .thenCompose { Tasks.loadFeedUiContainer(url, it) }
                .thenAcceptAsync( {
                    showFeedsDetails(it)
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

    private fun showFeedsDetails(feedContainerToShow: FeedUiContainer) {
        feedContainer = feedContainerToShow
        binding.feedFeedName.visibility = View.VISIBLE
        binding.feedFeedName.text = feedContainer!!.name
        binding.feedSubscribeButton.isEnabled = true
        if(feedContainer!!.icon != null) {
            binding.feedFeedIcon.visibility = View.VISIBLE
            Tasks.loadImage(feedContainer!!.icon!!, binding.feedFeedIcon.layoutParams.width,
                binding.feedFeedIcon.layoutParams.height).thenAcceptAsync(
                { binding.feedFeedIcon.setImageBitmap(it) },
                ContextCompat.getMainExecutor(this))
        } else {
            binding.feedFeedIcon.visibility = View.GONE
        }
        binding.feedFeedDescription.text = feedContainer!!.description ?: feedContainer!!.name

        if (feedContainer!!.scans.isEmpty()) {
            binding.feedFeedScanInfo.text = resources.getString(R.string.never_scanned)
        } else if (feedContainer!!.scans.all { it.sucessfully }) {
            val dateTime = Formatter.dateToString(this, feedContainer!!.scans.first().scanDate)
            binding.feedFeedScanInfo.text = resources.getString(R.string.last_scan, dateTime)
        } else if (feedContainer!!.scans.any { it.sucessfully }) {
            val lastSuccess = feedContainer!!.scans.first { it.sucessfully }
            val lastFailure = feedContainer!!.scans.first { !it.sucessfully }

            if (lastSuccess.scanDate.after(lastFailure.scanDate)) {
                val dateTime = Formatter.dateToString(this, lastSuccess.scanDate)
                binding.feedFeedScanInfo.text = resources.getString(R.string.last_scan, dateTime)
            } else {
                val lastSucessDateTime = Formatter.dateToString(this, lastSuccess.scanDate)
                val lastFailureDateTime = Formatter.dateToString(this, lastFailure.scanDate)
                binding.feedFeedScanInfo.text = resources.getString(R.string.last_scan_failed_last_success, lastFailureDateTime, lastSucessDateTime)
            }

        } else if (feedContainer!!.scans.all { !it.sucessfully }) {
            val dateTime = Formatter.dateToString(this, feedContainer!!.scans.first().scanDate)
            binding.feedFeedScanInfo.text = resources.getString(R.string.last_scan_failed, dateTime)
        } else {
            // just for debugging should never happen
            throw IllegalArgumentException()
        }
    }

    private fun hideFeedDetails() {
        binding.feedFeedName.visibility = View.INVISIBLE
        binding.feedSubscribeButton.isEnabled = false
        binding.feedFeedDescription.visibility = View.GONE
        binding.feedFeedIcon.visibility = View.GONE
    }

    private fun activateProgressBar() {
        binding.feedLoadingProgressBar.visibility = View.VISIBLE
    }

    private fun deactivateProgressBar() {
        binding.feedLoadingProgressBar.visibility = View.INVISIBLE
    }
}
