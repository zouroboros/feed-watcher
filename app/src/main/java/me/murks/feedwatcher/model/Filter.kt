package me.murks.feedwatcher.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author zouroboros
 * @date 8/13/18.
 */
data class Filter(val type: FilterType, val parameter: List<FilterParameter>, val index: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            FilterType.valueOf(parcel.readString()),
            parcel.createTypedArrayList(FilterParameter),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type.name)
        parcel.writeTypedList(parameter)
        parcel.writeInt(index)
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

    fun filterItems(feed: Feed, feedName: String, items: List<FeedItem>): List<FeedItem> {
        return when (type) {
            FilterType.CONTAINS -> {
                val str = parameter(ContainsFilterModel.TEXT_PARAMETER).stringValue!!
                items.filter { it.title.contains(str, true)
                        || it.description.contains(str, true)  }
            }
            FilterType.FEED -> {
                if (feedName == parameter(FeedFilterModel.FEED_NAME_PARAMETER).stringValue) {
                    items
                } else {
                    emptyList()
                }
            }
        }
    }

    private fun parameter(name: String) = parameter.first { it.name == name }
}