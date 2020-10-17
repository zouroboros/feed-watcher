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
Copyright 2020 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Xml
import android.view.*
import android.widget.ProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import me.murks.feedwatcher.R
import me.murks.feedwatcher.io.FeedParser
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.Tasks
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

class FeedsFragment : FeedWatcherBaseFragment(),
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

        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.visibility = View.VISIBLE
        adapter.items = LinkedList()

        val client = OkHttpClient()

        Tasks.stream<Feed, FeedUiContainer>({ input ->
            val request = Request.Builder().url(input.url).build()
            FeedUiContainer(input.name, input.url, input.lastUpdate,
                    FeedParser(client.newCall(request).execute().body!!.byteStream(), Xml.newPullParser()))
        }, { adapter.append(it) }, { item, _ ->
            adapter.append(FeedUiContainer(item.name, null, null, item.url,
                    item.lastUpdate, false))
        }, { progressBar.visibility = View.GONE })
                .execute(*app.feeds().toTypedArray())
    }

    override fun onOpenFeed(feed: FeedUiContainer) {
        val intent = Intent(context, FeedActivity::class.java)

        intent.data = Uri.parse(feed.url.toString())
        activity?.startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_feeds_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val parentActivity = activity

        if(parentActivity is OverviewActivity) {
            parentActivity.navigateTo(item.itemId)
        }

        return super.onOptionsItemSelected(item)
    }
}
