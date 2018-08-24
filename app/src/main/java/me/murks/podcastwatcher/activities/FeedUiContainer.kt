package me.murks.podcastwatcher.activities

import android.graphics.Bitmap
import me.murks.podcastwatcher.model.Feed

/**
 * @author zouroboros
 * @date 8/15/18.
 */
data class FeedUiContainer(val feed: Feed, val name: String, val author: String, val icon: Bitmap?)