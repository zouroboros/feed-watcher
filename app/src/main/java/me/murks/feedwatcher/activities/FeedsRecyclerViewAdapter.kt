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
Copyright 2019 - 2021 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

import me.murks.feedwatcher.R
import me.murks.feedwatcher.databinding.FragmentFeedsListItemBinding
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.Tasks
import java.net.URL

/**
 * Adapter for displaying a list of feeds
 * @see [Feed]
 * @author zouroboros
 */
class FeedsRecyclerViewAdapter(listener: FeedListInteractionListener?)
    : ListRecyclerViewAdapter<FeedsRecyclerViewAdapter.ViewHolder, FeedUiContainer>(listOf()) {

    private val onClickListener: View.OnClickListener = View.OnClickListener { v ->
        val item = v.tag as FeedUiContainer
        listener?.onOpenFeed(item)
    }
    private val images: MutableMap<URL, Bitmap> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_feeds_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.feedName.text = item.name
        holder.feedIcon.setImageResource(R.drawable.ic_feed_icon)
        if(item.icon != null && images.containsKey(item.icon)) {
            holder.feedIcon.setImageBitmap(images[item.icon])
        } else if (item.icon != null){
            Tasks.loadImage(item.icon).thenAcceptAsync({
                val index = items.indexOfFirst { item.icon == it.icon }
                images[item.icon] = it
                notifyItemChanged(index)
            }, ContextCompat.getMainExecutor(holder.itemView.context))
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        private val binding = FragmentFeedsListItemBinding.bind(mView)
        val feedName: TextView = binding.feedName
        val feedIcon: ImageView = binding.feedIcon
    }

    interface FeedListInteractionListener {
        fun onOpenFeed(feed: FeedUiContainer)
    }
}
