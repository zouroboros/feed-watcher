package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import me.murks.feedwatcher.activities.FeedUiContainer
import me.murks.feedwatcher.activities.FeedsRecyclerViewAdapter
import me.murks.feedwatcher.io.loadFeedUiContainer
import me.murks.feedwatcher.model.Feed

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