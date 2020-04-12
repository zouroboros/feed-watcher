package me.murks.feedwatcher.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_feed_import_list_item.view.*
import me.murks.feedwatcher.R
import me.murks.jopl.OpOutline

/**
 * @author zouroboros
 */
class FeedImportRecyclerViewAdapter(outlines: List<OpOutline>):
        ListRecyclerViewAdapter<FeedImportRecyclerViewAdapter.ViewHolder, OpOutline>(outlines) {

    val selectedOutlines = mutableSetOf<OpOutline>()

    inner class ViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val text = mView.activity_feed_import_list_item_text
        val checkBox = mView.activity_feed_import_list_item_checkbox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_feed_import_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position].title
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                selectedOutlines.add(items[holder.adapterPosition])
            } else {
                selectedOutlines.remove(items[holder.adapterPosition])
            }
        }
    }
}