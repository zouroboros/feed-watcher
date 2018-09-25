package me.murks.feedwatcher.activities

import android.support.v7.widget.RecyclerView
import java.util.*

/**
 * Base class for list adapter
 * @author zouroboros
 */
abstract class ListRecyclerViewAdapter<V : RecyclerView.ViewHolder, I>(items: List<I>):
        RecyclerView.Adapter<V>() {

    var _list = LinkedList(items)

    var items: List<I>
        set(value) {
            _list = LinkedList(value)
            notifyDataSetChanged()
        }
        get() = _list

    final override fun getItemCount() = items.size

    fun append(item: I) {
        _list.add(item)
        notifyItemInserted(_list.size - 1)
    }
}