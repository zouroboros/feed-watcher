package me.murks.podcastwatcher.tasks

import android.os.AsyncTask
import com.rometools.rome.io.ParsingFeedException
import me.murks.podcastwatcher.Either
import me.murks.podcastwatcher.Left
import me.murks.podcastwatcher.Right
import me.murks.podcastwatcher.activities.FeedUiContainer
import me.murks.podcastwatcher.activities.FeedsRecyclerViewAdapter
import me.murks.podcastwatcher.io.loadFeedUiContainer
import me.murks.podcastwatcher.model.Feed
import java.io.IOException
import java.net.URL

/**
 * Task for loading the details of feeds
 * @author zouroboros
 */
class FeedUrlTask(private val receiver: FeedUrlTaskReceiver) : AsyncTask<URL, Either<Exception, FeedUiContainer>, Unit>() {
    override fun doInBackground(vararg urls: URL) {
        for (url in urls) {
            println(url)
            try {
                publishProgress(Right(loadFeedUiContainer(url)))
                println(url)
            } catch (e: IOException) {
                publishProgress(Left(e))
            } catch (e: ParsingFeedException) {
                publishProgress(Left(e))
            }
        }


    }

    override fun onProgressUpdate(vararg values: Either<Exception, FeedUiContainer>) {
        super.onProgressUpdate(*values)
        for (value in values) {
            if(value.isLeft()) {
                receiver.feedFailed((value as Left).value)
            } else {
                receiver.feedLoaded((value as Right).value)
            }
        }
    }

    interface FeedUrlTaskReceiver {
        fun feedLoaded(feed: FeedUiContainer)
        fun feedFailed(e: Exception)
    }
}