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
Copyright 2020 - 2021 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Xml
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import me.murks.feedwatcher.R
import me.murks.feedwatcher.databinding.ActivityFeedExportBinding
import me.murks.feedwatcher.model.Feed
import me.murks.feedwatcher.tasks.ActionTask
import me.murks.feedwatcher.tasks.ErrorHandlingTaskListener
import me.murks.jopl.OpWriter
import java.io.FileWriter

/**
 * Activity for exporting feeds.
 *
 * @author zouroboros
 */
class FeedExportActivity : FeedWatcherBaseActivity() {
    companion object {
        const val FEED_EXPORT_SELECT_FILE_REQUEST_CODE = 1112
    }

    private lateinit var adapter: FeedExportRecyclerViewAdapter
    private lateinit var binding: ActivityFeedExportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedExportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityFeedExportSelectAllCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                adapter.selectAll()
            } else if (adapter.selectedItems.count() == adapter.itemCount) {
                adapter.deselectAll()
            }
        }

        binding.activityFeedExportSelectAllText.setOnClickListener {
            binding.activityFeedExportSelectAllCheckbox.isChecked =
                    !binding.activityFeedExportSelectAllCheckbox.isChecked
        }

        ActionTask({
            app.feeds()
        }, object : ErrorHandlingTaskListener<List<Feed>, List<Feed>, java.lang.Exception> {
            override fun onSuccessResult(result: List<Feed>) {
                adapter = FeedExportRecyclerViewAdapter(result)
                adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onChanged() {
                        binding.activityFeedExportSelectAllCheckbox.isChecked =
                                adapter.selectedItems.count() == adapter.itemCount
                    }

                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                        onChanged()
                    }
                })

                binding.activityFeedExportFeeds.layoutManager =
                        androidx.recyclerview.widget.LinearLayoutManager(binding.activityFeedExportFeeds.context)
                binding.activityFeedExportFeeds.adapter = adapter
                binding.activityFeedExportButton.isEnabled = true
                binding.activityFeedExportProgressBar.visibility = View.INVISIBLE
            }

            override fun onErrorResult(error: java.lang.Exception) {
                errorDialog(R.string.feed_import_open_opml_failed, error.localizedMessage,
                    { _, _ -> finish() })
                binding.activityFeedExportProgressBar.visibility = View.INVISIBLE
            }

            override fun onProgress(progress: List<Feed>) {}
        }).execute()

        binding.activityFeedExportButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_CREATE_DOCUMENT
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent,
                    resources.getString(R.string.select_file_to_export_feeds)),
                    FEED_EXPORT_SELECT_FILE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FEED_EXPORT_SELECT_FILE_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                val selectedFile = data!!.data!!
                ActionTask({
                    val outlines = app.exportFeeds(getString(R.string.feedwatcher_feed_export))
                    val file = contentResolver.openFileDescriptor(selectedFile, "w")
                    FileWriter(file!!.fileDescriptor).use {
                        val xmlSerializer = Xml.newSerializer()
                        xmlSerializer.setOutput(it)
                        OpWriter().write(outlines.opFile, xmlSerializer)
                    }
                }, object : ErrorHandlingTaskListener<Unit, Unit, java.lang.Exception> {

                    override fun onSuccessResult(result: Unit) {
                        Toast.makeText(this@FeedExportActivity, R.string.feeds_exported,
                                Toast.LENGTH_SHORT).show()
                        openFeeds()
                    }

                    override fun onErrorResult(error: java.lang.Exception) {
                        errorDialog(R.string.exporting_the_selected_feeds_failed, error.localizedMessage)
                    }

                    override fun onProgress(progress: Unit) {}
                }).execute()
            }
        }
    }
}
