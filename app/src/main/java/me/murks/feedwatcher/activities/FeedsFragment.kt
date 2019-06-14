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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import me.murks.feedwatcher.R
import me.murks.feedwatcher.tasks.ErrorHandlingTaskListener
import me.murks.feedwatcher.tasks.FeedDetailsTask
import java.io.IOException
import java.util.*

class FeedsFragment : FeedWatcherBaseFragment(),
        ErrorHandlingTaskListener<FeedUiContainer, Unit, IOException>,
        FeedsRecyclerViewAdapter.FeedListInteractionListener {

    private lateinit var progressBar: ProgressBar
    private lateinit var list: androidx.recyclerview.widget.RecyclerView
    private lateinit var adapter: ListRecyclerViewAdapter<FeedsRecyclerViewAdapter.ViewHolder, FeedUiContainer>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_feeds_list, container, false)

        adapter = FeedsRecyclerViewAdapter(this)

        progressBar = view.findViewById(R.id.feeds_fragment_progress_bar)
        list = view.findViewById(R.id.feeds_fragment_list)

        progressBar.visibility = View.INVISIBLE

        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter

        list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        return view
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.VISIBLE
        adapter.items = LinkedList()
        FeedDetailsTask(this).execute(*app.feeds().toTypedArray())
    }

    override fun onSuccessResult(result: Unit) {
        progressBar.visibility = View.GONE
    }

    override fun onErrorResult(error: IOException) {
        // TODO error handling
        progressBar.visibility = View.GONE
    }

    override fun onProgress(progress: FeedUiContainer) {
        adapter.append(progress)
    }

    override fun onOpenFeed(feed: FeedUiContainer) {
        val intent = Intent(context, FeedActivity::class.java)
        intent
        intent.data = Uri.parse(feed.url.toString())
        activity?.startActivity(intent)
    }

}
