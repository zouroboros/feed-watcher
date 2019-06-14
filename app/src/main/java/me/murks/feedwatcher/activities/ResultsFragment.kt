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
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import me.murks.feedwatcher.R

import me.murks.feedwatcher.model.Result
import me.murks.feedwatcher.tasks.ErrorHandlingTaskListener
import java.lang.Exception
import java.util.*

/**
 * Fragment representing a list of results. Activities that show
 * this fragment must implement the
 * [ResultsFragment.OnListFragmentInteractionListener] interface.
 */
class ResultsFragment : FeedWatcherBaseFragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var adapter: ResultsRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_results_list, container, false) as RecyclerView

        adapter = ResultsRecyclerViewAdapter(emptyList(), listener)

        view.layoutManager = LinearLayoutManager(context)
        view.addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))

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
        view.adapter = adapter

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    private fun load() {
        app.results(object : ErrorHandlingTaskListener<List<Result>, List<Result>, Exception>{
            override fun onSuccessResult(result: List<Result>) {
                adapter.items = LinkedList(result)
            }

            override fun onErrorResult(error: Exception) {
                error.printStackTrace()
            }

            override fun onProgress(progress: List<Result>) {}

        }).execute()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    interface OnListFragmentInteractionListener {
        fun onOpenResult(result: Result)
    }

}
