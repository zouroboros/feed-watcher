package me.murks.feedwatcher.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.ErrorHandlingTaskListener
import me.murks.feedwatcher.tasks.FeedDetailsTask
import java.io.IOException

class FeedsFragment : FeedWatcherBaseFragment(), ErrorHandlingTaskListener<FeedUiContainer, Unit, IOException>, FeedsRecyclerViewAdapter.FeedListInteractionListener {
    private lateinit var progressBar: ProgressBar
    private lateinit var list: RecyclerView
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

        return view
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.VISIBLE
        adapter.items = listOf()
        FeedDetailsTask(this).execute(*app.feeds().toTypedArray())
    }

    override fun onSuccessResult(result: Unit) {
        progressBar.visibility = View.INVISIBLE
    }

    override fun onErrorResult(error: IOException) {
        // TODO error handling
        progressBar.visibility = View.INVISIBLE
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
