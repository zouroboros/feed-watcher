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
        val connection = openConnection().track() as HttpURLConnection
        if(listOf(HttpURLConnection.HTTP_MOVED_PERM, HttpsURLConnection.HTTP_MOVED_TEMP,
                        HttpsURLConnection.HTTP_SEE_OTHER).contains(connection.responseCode)) {
            val newUrl = URL(connection.getHeaderField("Location"))
            connection.disconnect()
            return@using newUrl.finalUrl(maxDepth - 1)
        }
        return@using url
    }
}