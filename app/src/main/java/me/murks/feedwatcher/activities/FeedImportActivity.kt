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

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.murks.feedwatcher.R

import kotlinx.android.synthetic.main.activity_feed_import.*
import me.murks.feedwatcher.tasks.ActionTask
import me.murks.feedwatcher.tasks.ErrorHandlingTaskListener

import me.murks.jopl.Jopl
import me.murks.jopl.Outlines
import java.io.FileInputStream

class FeedImportActivity : FeedWatcherBaseActivity() {

    private lateinit var adapter: FeedImportRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_import)

        activity_feed_import_select_all_checkbox.setOnCheckedChangeListener {
            _, isChecked -> if (isChecked) {
                adapter.selectAll()
            } else if (adapter.selectedItems.count() == adapter.itemCount) {
                adapter.deselectAll()
            }
        }

        activity_feed_import_select_all_text.setOnClickListener { view ->
            activity_feed_import_select_all_checkbox.isChecked =
                    !activity_feed_import_select_all_checkbox.isChecked }

       intent.data?.also { fileUri ->
           activity_feed_import_progress_bar.visibility = View.VISIBLE

           ActionTask({
               val file = contentResolver.openFileDescriptor(fileUri, "r")
               return@ActionTask FileInputStream(file.fileDescriptor).use { Jopl.outlines(it) }
           }, object: ErrorHandlingTaskListener<Outlines, Outlines, java.lang.Exception> {
               override fun onSuccessResult(result: Outlines) {
                   adapter = FeedImportRecyclerViewAdapter(result.outlines)
                   adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
                       override fun onChanged() {
                           activity_feed_import_select_all_checkbox.isChecked =
                                   adapter.selectedItems.count() == adapter.itemCount
                       }

                       override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                           onChanged()
                       }
                   })

                   activity_feed_import_feeds.layoutManager =
                           androidx.recyclerview.widget.LinearLayoutManager(activity_feed_import_feeds.context)
                   activity_feed_import_feeds.adapter = adapter
                   activity_feed_import_button.isEnabled = true
                   activity_feed_import_progress_bar.visibility = View.INVISIBLE
               }

               override fun onErrorResult(error: java.lang.Exception) {
                   errorDialog(R.string.feed_import_open_opml_failed, error.localizedMessage,
                           DialogInterface.OnClickListener { _, _ -> finish() })
                   activity_feed_import_progress_bar.visibility = View.INVISIBLE
               }

               override fun onProgress(progress: Outlines) { }
           }).execute()
       }

        activity_feed_import_button.setOnClickListener {
            ActionTask({
                app.import(adapter.selectedItems)
            }, object: ErrorHandlingTaskListener<Unit, Unit, java.lang.Exception> {
                override fun onSuccessResult(result: Unit) {
                    it.context.openFeeds()
                }

                override fun onErrorResult(error: java.lang.Exception) {
                    it.context.errorDialog(R.string.feed_import_import_failed, error.localizedMessage)
                }

                override fun onProgress(progress: Unit) {}
            }).execute()
        }
    }
}
