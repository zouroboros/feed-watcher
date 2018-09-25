package me.murks.feedwatcher.activities

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_feeds.view.*
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.FeedDetailsTask
import java.util.*

class FeedsRecyclerViewAdapter()
    : ListRecyclerViewAdapter<FeedsRecyclerViewAdapter.ViewHolder, FeedUiContainer>(listOf()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_feeds, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.feedName.text = item.name
        holder.feedAuthor.text = item.author
        if(item.icon != null) {
            holder.feedIcon.setImageBitmap(item.icon)
        }

        with(holder.mView) {
            tag = item
        }
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val feedName: TextView = mView.feed_name!!
        val feedAuthor: TextView = mView.feed_author!!
        val feedIcon: ImageView = mView.feed_icon!!
    }
}
