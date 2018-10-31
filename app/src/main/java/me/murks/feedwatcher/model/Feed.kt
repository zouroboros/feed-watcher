package me.murks.feedwatcher.model

import android.os.Parcel
import android.os.Parcelable
import java.net.URL
import java.util.*

/**
 * @author zouroboros
 * @date 8/13/18.
 */
class Feed(val url: URL, val lastUpdate : Date, val name: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            URL(parcel.readString()),
            Date(parcel.readLong()),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url.toString())
        parcel.writeLong(lastUpdate.time)
        parcel.writeString(name)
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