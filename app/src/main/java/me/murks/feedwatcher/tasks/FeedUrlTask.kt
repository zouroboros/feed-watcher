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
import android.util.Xml
import me.murks.feedwatcher.Either
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.Right
import me.murks.feedwatcher.activities.FeedUiContainer
import me.murks.feedwatcher.io.FeedIO
import me.murks.feedwatcher.io.finalUrl
import me.murks.feedwatcher.model.Feed
import java.io.IOException
import java.lang.IllegalArgumentException
import java.net.URL

/**
 * Task for loading the details of feeds
 * @author zouroboros
 */
class FeedUrlTask(private val receiver: FeedUrlTaskReceiver, private val feeds: List<Feed>) : AsyncTask<URL, Either<Exception, FeedUiContainer>, Unit>() {
    override fun doInBackground(vararg urls: URL) {
        for (url in urls) {
            try {
                val existingFeed = feeds.find { it.url == url }
                if (existingFeed != null) {
                    publishProgress(Right(FeedUiContainer(existingFeed.name, existingFeed.url,
                            existingFeed.lastUpdate,
                            FeedIO(existingFeed.url.finalUrl().openStream(), Xml.newPullParser()))))
                } else {
                    publishProgress(Right(FeedUiContainer(url, null,
                            FeedIO(url.finalUrl().openStream(), Xml.newPullParser()))))
                }
            } catch (e: IOException) {
                publishProgress(Left(e))
            } catch (e: Exception) {
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