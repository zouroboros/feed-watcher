package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import com.rometools.rome.io.ParsingFeedException
import me.murks.feedwatcher.Either
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.Right
import me.murks.feedwatcher.activities.FeedUiContainer
import me.murks.feedwatcher.io.loadFeedUiContainer
import java.io.IOException
import java.lang.IllegalArgumentException
import java.net.URL

/**
 * Task for loading the details of feeds
 * @author zouroboros
 */
class FeedUrlTask(private val receiver: FeedUrlTaskReceiver) : AsyncTask<URL, Either<Exception, FeedUiContainer>, Unit>() {
    override fun doInBackground(vararg urls: URL) {
        for (url in urls) {
            try {
                publishProgress(Right(loadFeedUiContainer(url)))
            } catch (e: IOException) {
                publishProgress(Left(e))
            } catch (e: ParsingFeedException) {
                publishProgress(Left(e))
            } catch (e: IllegalArgumentException) {
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