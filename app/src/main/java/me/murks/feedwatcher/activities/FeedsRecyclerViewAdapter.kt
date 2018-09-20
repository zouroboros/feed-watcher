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

class FeedsRecyclerViewAdapter(private val feeds: List<Feed>)
    : RecyclerView.Adapter<FeedsRecyclerViewAdapter.ViewHolder>() {

    private val feedsUiContainer: MutableList<FeedUiContainer>

    init {
        feedsUiContainer = LinkedList()
        FeedDetailsTask(this).execute(*feeds.toTypedArray());
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_feeds, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = feedsUiContainer[position]
        holder.feed_name.text = item.name
        holder.feed_author.text = item.author
        if(item.icon != null) {
            holder.feedIcon.setImageBitmap(item.icon)
        }

        with(holder.mView) {
            tag = item
        }
    }

    override fun getItemCount(): Int = feedsUiContainer.size

    fun appendFeed(feed: FeedUiContainer) {
        feedsUiContainer.add(feed)
        notifyItemInserted(feedsUiContainer.size - 1)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val feed_name: TextView = mView.feed_name
        val feed_author: TextView = mView.feed_author
        val feedIcon: ImageView = mView.feed_icon
    }
}
