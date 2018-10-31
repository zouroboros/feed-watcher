package me.murks.feedwatcher.activities

import android.os.Parcel
import android.os.Parcelable
import me.murks.feedwatcher.model.ContainsFilter
import me.murks.feedwatcher.model.Filter
import me.murks.feedwatcher.model.FilterType
import me.murks.feedwatcher.model.FilterTypeCallback
import java.lang.IllegalArgumentException

/**
 * @author zouroboros
 */
class FilterParcel(val parcel: Parcel): FilterTypeCallback<Unit>, Parcelable.Creator<Filter> {

    private fun genericFields(filter: Filter) {
        parcel.writeInt(FilterType.values().indexOf(filter.type))
        parcel.writeInt(filter.index)
    }

    override fun filter(filter: ContainsFilter) {
        parcel.writeString(filter.text)
    }

    override fun createFromParcel(source: Parcel): Filter {
        val type = FilterType.values()[source.readInt()]
        val index = source.readInt()
        if(type == FilterType.CONTAINS) {
            return ContainsFilter(index, source.readString())
        }
        throw IllegalArgumentException()
    }

    override fun newArray(size: Int): Array<Filter?> {
        return arrayOfNulls(size)
    }
}