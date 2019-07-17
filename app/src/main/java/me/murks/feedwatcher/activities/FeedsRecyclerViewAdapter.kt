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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_feeds_list_item.view.*
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.ImageViewTask

/**
 * Adapter for displaying a list of feeds
 * @see [Feed]
 * @author zouroboros
 */
class FeedsRecyclerViewAdapter(listener: FeedListInteractionListener?)
    : ListRecyclerViewAdapter<FeedsRecyclerViewAdapter.ViewHolder, FeedUiContainer>(listOf()) {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as FeedUiContainer
            listener?.onOpenFeed(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_feeds_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.feedName.text = item.name
        holder.feedAuthor.text = item.author
        if(item.icon != null) {
            ImageViewTask(holder.feedIcon).execute(item.icon)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val feedName: TextView = mView.feed_name!!
        val feedAuthor: TextView = mView.feed_author!!
        val feedIcon: ImageView = mView.feed_icon!!
    }

    interface FeedListInteractionListener {
        fun onOpenFeed(feed: FeedUiContainer)
    }
}
