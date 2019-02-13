package me.murks.feedwatcher.activities

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.R

import me.murks.feedwatcher.model.Query
import java.util.*

/**
 * A fragment representing a list of Queries.
 */
class QueriesFragment : FeedWatcherBaseFragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var adapter: QueryRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_queries_list, container, false) as RecyclerView

        adapter = QueryRecyclerViewAdapter(app.queries(), listener)

        view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.adapter = adapter

        view.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val swipeHelper = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    override fun onMove(recyclerView: RecyclerView,
                                        viewHolder: RecyclerView.ViewHolder,
                                        target: RecyclerView.ViewHolder): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        app.delete(adapter.items[viewHolder.adapterPosition])
                        adapter.items.removeAt(viewHolder.adapterPosition)
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                    }
                })

        swipeHelper.attachToRecyclerView(view)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()
        adapter.items = LinkedList(app.queries())
    }

    interface OnListFragmentInteractionListener {
        fun onOpenQuery(item: Query)
    }

}
