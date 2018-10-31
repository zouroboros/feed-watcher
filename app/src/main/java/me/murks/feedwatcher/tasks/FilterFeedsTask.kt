package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import com.rometools.rome.io.ParsingFeedException
import me.murks.feedwatcher.*
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
                val items = queries.associateBy({query -> query},
                        {query -> query.filter.fold(items(feed.url, feed.lastUpdate))
                            {acc, filter -> filter.filterItems(feed, acc)}})
                        .entries.map { it.value.map { item -> AbstractMap.SimpleEntry(it.key, item) } }
                        .flatten()
                        .groupBy({ it.value }) { it.key }

                items.entries.forEach {
                    val result = Result(0, feed, it.value, it.key, Date())
                    publishProgress(result)
                    app.addResult(result)
                    allResults.add(result)
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