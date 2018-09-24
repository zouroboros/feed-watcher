package me.murks.feedwatcher.activities

import android.support.v7.widget.RecyclerView

/**
 * Base class for list adapter
 * @author zouroboros
 */
abstract class ListRecyclerViewAdapter<V : RecyclerView.ViewHolder, I>(items: List<I>):
        RecyclerView.Adapter<V>() {

    var items: List<I> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    final override fun getItemCount() = items.size
}