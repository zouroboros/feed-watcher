package me.murks.feedwatcher.activities

import android.graphics.Bitmap
import java.net.URL
import java.util.*

/**
 * @author zouroboros
 * @date 8/15/18.
 */
data class FeedUiContainer(val name: String, val author: String, val icon: Bitmap?,
                           val description: String, val url: URL, val updated: Date?)