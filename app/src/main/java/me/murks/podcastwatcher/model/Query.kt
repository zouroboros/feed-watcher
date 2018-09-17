package me.murks.podcastwatcher.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author zouroboros
 * @date 8/17/18.
 */
data class Query(val id: Long, val name: String, val filter: List<Filter>): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.createTypedArrayList(Filter)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeTypedList(filter)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Query> {
        override fun createFromParcel(parcel: Parcel): Query {
            return Query(parcel)
        }

        override fun newArray(size: Int): Array<Query?> {
            return arrayOfNulls(size)
        }
    }
}