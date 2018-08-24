package me.murks.podcastwatcher.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author zouroboros
 * @date 8/13/18.
 */
data class Filter(val type: FilterType, val parameter: List<FilterParameter>) : Parcelable {
    constructor(parcel: Parcel) : this(
            FilterType.valueOf(parcel.readString()),
            parcel.createTypedArrayList(FilterParameter)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type.name)
        parcel.writeTypedList(parameter)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Filter> {
        override fun createFromParcel(parcel: Parcel): Filter {
            return Filter(parcel)
        }

        override fun newArray(size: Int): Array<Filter?> {
            return arrayOfNulls(size)
        }
    }
}