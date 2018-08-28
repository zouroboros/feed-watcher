package me.murks.podcastwatcher.activities

import android.graphics.Bitmap
import me.murks.podcastwatcher.model.Feed
import java.net.URL

/**
 * @author zouroboros
 * @date 8/15/18.
 */
data class FeedUiContainer(val name: String, val author: String, val icon: Bitmap?,
                           val description: String, val url: URL)