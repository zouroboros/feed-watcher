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
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.tasks

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import me.murks.feedwatcher.Either
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.Right
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URL

/**
 * @author zouroboros
 */
class LoadImageTask(private val listener: ErrorHandlingTaskListener<Pair<URL, Bitmap>, Void, IOException>) : AsyncTask<URL, Either<IOException, Pair<URL, Bitmap>>, Void>() {

    override fun doInBackground(vararg urls: URL): Void? {
        val client = OkHttpClient()
        for (url in urls) {
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().body?.byteStream().use {
                    publishProgress(Right(Pair(url, BitmapFactory.decodeStream(it))))
                }
            } catch (ioe: IOException) {
                publishProgress(Left(ioe))
            }
        }
        return null
    }

    override fun onProgressUpdate(vararg values: Either<IOException, Pair<URL, Bitmap>>) {
        for (value in values) {
             when (value) {
                is Right -> listener.onProgress(value.value)
                 is Left -> listener.onErrorResult(value.value)
            }
        }
    }
}