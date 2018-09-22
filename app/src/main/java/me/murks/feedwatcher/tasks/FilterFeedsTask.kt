package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import com.rometools.rome.io.ParsingFeedException
import me.murks.feedwatcher.Either
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.Right
import me.murks.feedwatcher.io.items
import me.murks.feedwatcher.io.loadFeedUiContainer
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.model.Result
import java.io.IOException
import java.util.*

/**
 * @author zouroboros
 */
class FilterFeedsTask(private val app: FeedWatcherApp,
                      private val listener: TaskListener<Result, Either<Exception, List<Result>>>)
    : AsyncTask<Feed, Result, Either<Exception, List<Result>>>() {

    override fun doInBackground(vararg feeds: Feed): Either<Exception, List<Result>> {
        val queries = app.queries()

        val allResults = LinkedList<Result>()
        
        try {
            for (feed in feeds) {
                val feedName = loadFeedUiContainer(feed).name
                for (query in queries){
                    val items = items(feed.url, feed.lastUpdate)
                    val found = query.filter.fold(items) { acc, filter -> filter.filterItems(feed, feedName, acc) }
                    if (found.isNotEmpty()) {
                        val results = found.map { Result(feed, query, it, Date(), feedName) }
                        for (result in results) {
                            publishProgress(result)
                            app.addResult(result)
                        }
                        allResults.addAll(results)
                    }
                }
            }
            return Right(allResults)
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            return Left(ioe)
        } catch (e: ParsingFeedException) {
            return Left(e)
        }
    }

    override fun onPostExecute(result: Either<Exception, List<Result>>) {
        listener.onResult(result)
        super.onPostExecute(result)
    }

    override fun onProgressUpdate(vararg values: Result) {
        for (result in values) {
            listener.onProgress(result)
        }
        super.onProgressUpdate(*values)
    }
}