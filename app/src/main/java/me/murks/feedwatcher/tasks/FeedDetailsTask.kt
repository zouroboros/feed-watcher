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
along with FeedWatcher.  If not, see <https://www.gnu.org/licenses/>.
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import me.murks.feedwatcher.Either
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.Right
import me.murks.feedwatcher.activities.FeedUiContainer
import me.murks.feedwatcher.io.FeedIO
import me.murks.feedwatcher.io.finalUrl
import me.murks.feedwatcher.model.Feed
import java.io.IOException

/**
 * Task for loading the details of feeds
 * @author zouroboros
 */
class FeedDetailsTask(listener: ErrorHandlingTaskListener<FeedUiContainer, Unit, IOException>):
        AsyncTask<Feed, FeedUiContainer, Either<IOException, Unit>>() {

    private val _listener = ErrorHandlingTaskListenerWrapper(listener)

    override fun doInBackground(vararg feeds: Feed): Either<IOException, Unit> {
        try {
            feeds.forEach {
                publishProgress(FeedUiContainer(it.name, it.url, it.lastUpdate,
                        FeedIO(it.url.finalUrl().openStream())))
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