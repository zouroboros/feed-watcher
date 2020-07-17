package me.murks.feedwatcher.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_feed_import_list_item.view.*
import me.murks.feedwatcher.R
import me.murks.jopl.OpOutline

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