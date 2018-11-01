package me.murks.feedwatcher.activities

import androidx.recyclerview.widget.RecyclerView
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
