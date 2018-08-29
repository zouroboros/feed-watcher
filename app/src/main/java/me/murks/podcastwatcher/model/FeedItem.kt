package me.murks.podcastwatcher.model

import android.os.Parcel
import android.os.Parcelable
import java.net.URL
import java.util.*

/**
 * @author zouroboros
 */
data class FeedItem(val title: String, val description: String, val link: URL?, val date: Date) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            if (parcel.readByte() > 0) URL(parcel.readString()) else null,
            Date(parcel.readLong())) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        if(link != null) {
            parcel.writeByte(1)
            parcel.writeString(link.toString())
        } else {
            parcel.writeByte(0)
        }
        parcel.writeLong(date.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedItem> {
        override fun createFromParcel(parcel: Parcel): FeedItem {
            return FeedItem(parcel)
        }

        override fun newArray(size: Int): Array<FeedItem?> {
            return arrayOfNulls(size)
        }
    }
}