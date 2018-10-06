package me.murks.feedwatcher.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * @author zouroboros
 */
data class Result(val id: Long, val feed: Feed, val queries: Collection<Query>, val item: FeedItem,
                  val found: Date, val feedName: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readParcelable(Feed::class.java.classLoader),
            parcel.readArrayList(Query::class.java.classLoader).map { it as Query },
            parcel.readParcelable(FeedItem::class.java.classLoader),
            Date(parcel.readLong()),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(feed, flags)
        parcel.writeList(queries.toList())
        parcel.writeParcelable(item, flags)
        parcel.writeLong(found.time)
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