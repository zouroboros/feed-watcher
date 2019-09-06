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
along with FeedWatcher. If not, see <https://www.gnu.org/licenses/>.
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.activities

import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Base class for list adapter
 * @author zouroboros
 */
abstract class ListRecyclerViewAdapter<V : RecyclerView.ViewHolder, I>(items: List<I>):
        RecyclerView.Adapter<V>() {

    private var list: MutableList<I> = LinkedList(items)

    var items: MutableList<I>
        set(value) {
            list = value
            notifyDataSetChanged()
        }
        get() = list

    final override fun getItemCount() = items.size

    fun append(item: I) {
        list.add(item)
        notifyItemInserted(list.size - 1)
    }
}