/*
This file is part of FeedWatcher.

FeedWatcher is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FeedWatcher is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FeedWatcher. If not, see <https://www.gnu.org/licenses/>.
Copyright 2019-2020 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import me.murks.feedwatcher.R

import me.murks.feedwatcher.model.Query
import me.murks.feedwatcher.tasks.Tasks

/**
 * A fragment representing a list of Queries.
 */
class QueriesFragment : FeedWatcherBaseFragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var adapter: QueryRecyclerViewAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_queries_list, container, false)
        progressBar = view.findViewById(R.id.queries_fragment_progress_bar)
        val queryList = view.findViewById<RecyclerView>(R.id.queries_fragment_query_list)

        adapter = QueryRecyclerViewAdapter(app.queries(), listener)

        queryList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        queryList.adapter = adapter

        queryList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

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

        swipeHelper.attachToRecyclerView(queryList)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.visibility = View.VISIBLE
        Tasks.run<Unit, List<Query>>({app.queries()}, {
            adapter.items = it.toMutableList()
            progressBar.visibility = View.GONE
        }, {
            it.printStackTrace()
        }).execute(Unit)
    }

    interface OnListFragmentInteractionListener {
        fun onOpenQuery(item: Query)
    }

}
