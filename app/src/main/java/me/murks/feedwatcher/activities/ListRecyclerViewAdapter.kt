package me.murks.feedwatcher.activities

import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Base class for list adapter
 * @author zouroboros
 */
abstract class ListRecyclerViewAdapter<V : androidx.recyclerview.widget.RecyclerView.ViewHolder, I>(items: List<I>):
        androidx.recyclerview.widget.RecyclerView.Adapter<V>() {

    private var list = LinkedList(items)

    var items: List<I>
        set(value) {
            list = LinkedList(value)
            notifyDataSetChanged()
        }
        get() = list

    final override fun getItemCount() = items.size

    fun append(item: I) {
        list.add(item)
        notifyItemInserted(list.size - 1)
    }
}