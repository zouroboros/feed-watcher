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
Copyright 2021 Zouroboros
 */
package me.murks.feedwatcher.activities

import androidx.recyclerview.widget.RecyclerView

/**
 * Abstract class that provides a basic framework for selectable lists.
 *
 * @author zouroboros
 */
abstract class SelectableRecyclerViewAdapter<TViewHolder: RecyclerView.ViewHolder, TItem>(
        items: List<TItem>):
        ListRecyclerViewAdapter<TViewHolder, TItem>(items) {

    val selectedItems = mutableSetOf<TItem>()

    fun select(itemIndex: Int) {
        if(selectedItems.add(items[itemIndex])) {
            notifyItemChanged(itemIndex)
        }
    }

    fun deselect(itemIndex: Int) {
        if(selectedItems.remove(items[itemIndex])) {
            notifyItemChanged(itemIndex)
        }
    }

    fun selectAll() {
        if(selectedItems.addAll(items)) {
            notifyDataSetChanged()
        }
    }

    fun deselectAll() {
        if(selectedItems.removeAll(items)) {
            notifyDataSetChanged()
        }
    }
}