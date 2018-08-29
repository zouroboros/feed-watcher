package me.murks.podcastwatcher.model

import android.os.Parcel
import android.os.Parcelable
import java.net.URL
import java.util.*

/**
 * @author zouroboros
 */
data class Result(val feed: Feed, val query: Query, val name: String, val description: String,
                  val found: Date, val link: URL?, val feedName: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Feed::class.java.classLoader),
            parcel.readParcelable(Query::class.java.classLoader),
            parcel.readString(),
            parcel.readString(),
            Date(parcel.readLong()),
            if (parcel.readByte() > 0) URL(parcel.readString()) else null,
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(feed, flags)
        parcel.writeParcelable(query, flags)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeLong(found.time)
        if(link != null) {
            parcel.writeByte(1)
            parcel.writeString(link.toString())
        } else {
            parcel.writeByte(0)
        }
        parcel.writeString(feedName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Result> {
        override fun createFromParcel(parcel: Parcel): Result {
            return Result(parcel)
        }

        override fun newArray(size: Int): Array<Result?> {
            return arrayOfNulls(size)
        }
    }
}