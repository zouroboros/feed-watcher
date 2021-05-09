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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.murks.feedwatcher.databinding.ActivityFeedExportListItemBinding
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Feed

/**
 * Adapter for selecting feeds for import
 * @author zouroboros
 */
class FeedExportRecyclerViewAdapter(feeds: List<Feed>):
        SelectableRecyclerViewAdapter<FeedExportRecyclerViewAdapter.ViewHolder, Feed>(feeds) {

    inner class ViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        private val binding = ActivityFeedExportListItemBinding.bind(mView)
        val text = binding.activityFeedExportListItemText
        val checkBox = binding.activityFeedExportListItemCheckbox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_feed_export_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position].name
        holder.checkBox.isChecked = selectedItems.contains(items[position])
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                select(holder.bindingAdapterPosition)
            } else {
                deselect(holder.bindingAdapterPosition)
            }
        }
    }
}