package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import me.murks.feedwatcher.Either
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.Right
import me.murks.feedwatcher.activities.FeedUiContainer
import me.murks.feedwatcher.io.loadFeedUiContainer
import me.murks.feedwatcher.model.Feed
import java.io.IOException

/**
 * Task for loading the details of feeds
 * @author zouroboros
 */
class FeedDetailsTask(listener: ErrorHandlingTaskListener<FeedUiContainer, Unit, IOException>):
        AsyncTask<Feed, FeedUiContainer, Either<IOException, Unit>>() {

    private val _listener = ErrorHandlingTaskListenerWrapper(listener)

    override fun doInBackground(vararg feeds: Feed?): Either<IOException, Unit> {
        try {
            feeds.forEach {
                publishProgress(loadFeedUiContainer(it!!))
            }
            return Right(Unit)
        } catch (e: IOException) {
            return Left(e)
        }

    }

    override fun onProgressUpdate(vararg values: FeedUiContainer) {
        for (value in values) {
            _listener.onProgress(value)
        }
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: Either<IOException, Unit>) {
        super.onPostExecute(result)
        _listener.onResult(result)
    }

}