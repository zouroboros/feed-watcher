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
import android.widget.ImageView
import java.net.URL

/**
 * @author zouroboros
 */
class ImageViewTask(private val imageView: ImageView) : AsyncTask<URL, Void, Bitmap>() {

    override fun doInBackground(vararg urls: URL): Bitmap? {
        urls.first().openStream().use {
            return BitmapFactory.decodeStream(it)
        }
    }

    override fun onPostExecute(result: Bitmap) {
        imageView.setImageBitmap(result)
    }
}