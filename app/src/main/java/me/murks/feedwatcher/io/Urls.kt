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
package me.murks.feedwatcher.io

import me.murks.feedwatcher.using
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Follows all redirects and returns the final url.
 * @param maxDepth maximum number of redirects to follow (defaults to 10)
 * @author zouroboros
 */
fun URL.finalUrl(maxDepth: Int = 10): URL {
    if(maxDepth == 0) {
        throw IOException("Exceeded maximum number of redirects")
    }

    val url = this
    return using {
        val connection = (openConnection() as HttpURLConnection).track()
        if(listOf(HttpURLConnection.HTTP_MOVED_PERM, HttpsURLConnection.HTTP_MOVED_TEMP,
                        HttpsURLConnection.HTTP_SEE_OTHER).contains(connection.responseCode)) {
            val newUrl = URL(connection.getHeaderField("Location"))
            return@using newUrl.finalUrl(maxDepth - 1)
        }
        return@using url
    }
}