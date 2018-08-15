package me.murks.podcastwatcher.tasks

import android.os.AsyncTask
import me.murks.podcastwatcher.FeedUiContainer
import me.murks.podcastwatcher.FeedsRecyclerViewAdapter
import me.murks.podcastwatcher.io.loadFeedUiContainer
import me.murks.podcastwatcher.model.Feed

/**
 * Task for loading the details of feeds
 * @author zouroboros
 */
class FeedDetailsTask(private val list: FeedsRecyclerViewAdapter) : AsyncTask<Feed, FeedUiContainer, Unit>() {
    override fun doInBackground(vararg feeds: Feed?): Unit {
        feeds.forEach {
            publishProgress(loadFeedUiContainer(it!!))
        }
    }

    override fun onProgressUpdate(vararg values: FeedUiContainer?) {
        val feed = values.get(0)!!
        list.appendFeed(feed)
        super.onProgressUpdate(*values)
    }

}