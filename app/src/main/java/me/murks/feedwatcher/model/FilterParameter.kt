package me.murks.feedwatcher.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author zouroboros
 * @date 8/13/18.
 */
data class FilterParameter(val name: String, var stringValue: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(stringValue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterParameter> {
        override fun createFromParcel(parcel: Parcel): FilterParameter {
            return FilterParameter(parcel)
        }

        override fun newArray(size: Int): Array<FilterParameter?> {
            return arrayOfNulls(size)
        }
    }
}