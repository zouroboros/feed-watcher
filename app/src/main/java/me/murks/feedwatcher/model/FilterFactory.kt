/*
This file is part of FeedWatcher.

FeedWatcher is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FeedWatcher is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FeedWatcher.  If not, see <https://www.gnu.org/licenses/>.
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.model

import java.lang.IllegalArgumentException
import java.net.URL

/**
 * Factory for [Filter]
 * @author zouroboros
 */
object FilterFactory {
    fun new(index: Int, type: FilterType, parameter: List<FilterParameter>): Filter {
        if(type == FilterType.CONTAINS) {
            val text = parameter.find { it.name == ContainsFilter.textParameterName }!!.stringValue
            return ContainsFilter(index, text)
        } else if (type == FilterType.FEED) {
            val feedUrl = parameter.find { it.name == FeedFilter.feedUrlParameterName }!!.stringValue
            return FeedFilter(index, if (feedUrl != null) URL(feedUrl) else null)
        } else if (type == FilterType.NEW) {
            val startDate = parameter.find { it.name == NewEntryFilter.startDateParameterName }!!.dateValue!!
            return NewEntryFilter(index, startDate)
        }
        throw IllegalArgumentException("Illegal filter type ${type}!")
    }
}