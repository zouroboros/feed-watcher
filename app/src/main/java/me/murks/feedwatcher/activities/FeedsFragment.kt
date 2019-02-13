package me.murks.feedwatcher.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.ErrorHandlingTaskListener
import me.murks.feedwatcher.tasks.FeedDetailsTask
import java.io.IOException
import java.util.*

class FeedsFragment : FeedWatcherBaseFragment(), ErrorHandlingTaskListener<FeedUiContainer, Unit, IOException>, FeedsRecyclerViewAdapter.FeedListInteractionListener {
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

        list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
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
