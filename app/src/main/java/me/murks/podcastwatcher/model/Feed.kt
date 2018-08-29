package me.murks.podcastwatcher.model

import android.os.Parcel
import android.os.Parcelable
import java.net.URL
import java.util.*

/**
 * @author zouroboros
 * @date 8/13/18.
 */
class Feed(val url: URL, val lastUpdate : Date) : Parcelable {
    constructor(parcel: Parcel) : this(
            URL(parcel.readString()),
            Date(parcel.readLong())) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url.toString())
        parcel.writeLong(lastUpdate.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Feed> {
        override fun createFromParcel(parcel: Parcel): Feed {
            return Feed(parcel)
        }

        override fun newArray(size: Int): Array<Feed?> {
            return arrayOfNulls(size)
        }
    }
}