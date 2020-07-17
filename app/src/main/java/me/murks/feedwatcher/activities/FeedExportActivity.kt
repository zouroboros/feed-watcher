package me.murks.feedwatcher.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_feed_export.*
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.ActionTask
import me.murks.feedwatcher.tasks.ErrorHandlingTaskListener

class FeedExportActivity : FeedWatcherBaseActivity() {

    private lateinit var adapter: FeedExportRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_export)

        activity_feed_export_select_all_checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                adapter?.selectAll()
            } else if (adapter.selectedItems.count() == adapter.itemCount) {
                adapter.deselectAll()
            }
        }

        activity_feed_export_select_all_text.setOnClickListener { view ->
            activity_feed_export_select_all_checkbox.isChecked =
                    !activity_feed_export_select_all_checkbox.isChecked
        }

        ActionTask({
            app.feeds()
        }, object : ErrorHandlingTaskListener<List<Feed>, List<Feed>, java.lang.Exception> {
            override fun onSuccessResult(feeds: List<Feed>) {
                adapter = FeedExportRecyclerViewAdapter(feeds)
                adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onChanged() {
                        activity_feed_export_select_all_checkbox.isChecked =
                                adapter.selectedItems.count() == adapter.itemCount
                    }

                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                        onChanged()
                    }
                })

                activity_feed_export_feeds.layoutManager =
                        androidx.recyclerview.widget.LinearLayoutManager(activity_feed_export_feeds.context)
                activity_feed_export_feeds.adapter = adapter
                activity_feed_export_button.isEnabled = true
                activity_feed_export_progress_bar.visibility = View.INVISIBLE
            }

            override fun onErrorResult(error: java.lang.Exception) {
                errorDialog(R.string.feed_import_open_opml_failed, error.localizedMessage,
                        DialogInterface.OnClickListener { dialog, which -> finish() })
                activity_feed_export_progress_bar.visibility = View.INVISIBLE
            }

            override fun onProgress(progress: List<Feed>) {}
        }).execute()

        activity_feed_export_button.setOnClickListener {
            ActionTask({
                // todo export feeds
            }, object : ErrorHandlingTaskListener<Unit, Unit, java.lang.Exception> {
                override fun onSuccessResult(result: Unit) {
                    it.context.openFeeds()
                }

                override fun onErrorResult(error: java.lang.Exception) {
                    it.context.errorDialog(R.string.exporting_the_selected_feeds_failed, error.localizedMessage)
                }

                override fun onProgress(progress: Unit) {}
            }).execute()
        }
    }
}
