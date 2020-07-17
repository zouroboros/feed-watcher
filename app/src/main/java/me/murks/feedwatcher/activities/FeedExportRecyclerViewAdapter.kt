package me.murks.feedwatcher.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_feed_export_list_item.view.*
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Feed

/**
 * Adapter for selecting feeds for import
 * @author zouroboros
 */
class FeedExportRecyclerViewAdapter(feeds: List<Feed>):
        SelectableRecyclerViewAdapter<FeedExportRecyclerViewAdapter.ViewHolder, Feed>(feeds) {

    inner class ViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val text = mView.activity_feed_export_list_item_text
        val checkBox = mView.activity_feed_export_list_item_checkbox
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
                select(holder.adapterPosition)
            } else {
                deselect(holder.adapterPosition)
            }
        }
    }
}