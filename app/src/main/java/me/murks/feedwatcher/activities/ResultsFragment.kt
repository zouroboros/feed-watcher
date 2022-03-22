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
Copyright 2019-2022 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ProgressBar
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import me.murks.feedwatcher.R

import me.murks.feedwatcher.model.Result
import me.murks.feedwatcher.tasks.Tasks
import kotlin.math.abs

// TODO add undo button for item deleted
/**
 * Fragment representing a list of results. Activities that show
 * this fragment must implement the
 * [ResultsFragment.OnListFragmentInteractionListener] interface.
 */
class ResultsFragment : FeedWatcherAsyncLoadingFragment<Result>() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var adapter: ResultsRecyclerViewAdapter
    private lateinit var progressBar: ProgressBar
    private var isReloading = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_results_list, container, false)

        progressBar = view.findViewById(R.id.results_fragment_progress_bar)
        val resultsList = view.findViewById<RecyclerView>(R.id.results_fragment_results_list)

        adapter = ResultsRecyclerViewAdapter(emptyList(), listener)

        resultsList.layoutManager = LinearLayoutManager(context)
        resultsList.addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))

        val swipeHelper = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    override fun onMove(recyclerView: RecyclerView,
                                        viewHolder: RecyclerView.ViewHolder,
                                        target: RecyclerView.ViewHolder): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        app.delete(adapter.items[viewHolder.bindingAdapterPosition])
                        adapter.items.removeAt(viewHolder.bindingAdapterPosition)
                        adapter.notifyItemRemoved(viewHolder.bindingAdapterPosition)
                    }
                })

        swipeHelper.attachToRecyclerView(resultsList)
        resultsList.adapter = adapter

        val gestureDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                if (abs(velocityX) < abs(velocityY) && velocityY > 0) {
                    scanFeeds()
                    return true
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })

        resultsList.setOnTouchListener { _, motionEvent ->
            val scrollPosition = (resultsList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (scrollPosition == 0) {
                return@setOnTouchListener gestureDetector.onTouchEvent(motionEvent)
            }

            return@setOnTouchListener false
        }

        setHasOptionsMenu(true)

        return view
    }

    private fun scanFeeds() {
        if(!isReloading) {
            isReloading = true
            startProgress()
            Tasks.filterFeeds(app).thenAccept {
                reload()
                isReloading = false
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reload()
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    /**
     * Activates the progress bar
     */
    private fun startProgress() {
        progressBar.visibility = View.VISIBLE
    }

    /**
     * Deactivates the progress bar
     */
    private fun stopProgress() {
        progressBar.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onOpenResult(result: Result)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_results_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.results_fragment_clear_results) {
            adapter.items.clear()
            adapter.notifyDataSetChanged()
            Snackbar.make(requireView(), resources.getString(R.string.results_cleared), Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        reload()
                    }
                    .addCallback(object: Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            Tasks.run { app.deleteResults() }
                        }
                    })
                    .show()
        } else if (item.itemId == R.id.results_fragment_scan_feeds) {
            scanFeeds()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun loadData() {
        for (result in app.results()) {
            publishResult(result)
        }
    }

    override fun processResult(result: Result) {
        adapter.append(result)
    }

    override fun onLoadingStart() {
        startProgress()
        adapter.items.clear()
        adapter.notifyDataSetChanged()
    }

    override fun onLoadingFinished() {
        stopProgress()
    }
}
