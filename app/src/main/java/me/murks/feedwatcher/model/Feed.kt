package me.murks.feedwatcher.model

import android.os.Parcel
import android.os.Parcelable
import java.net.URL
import java.util.*

/**
 * @author zouroboros
 * @date 8/13/18.
 */
class Feed(val url: URL, val lastUpdate : Date, val name: String)