package me.murks.feedwatcher.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import me.murks.feedwatcher.R

import kotlinx.android.synthetic.main.activity_feed_import.*

import me.murks.jopl.Jopl
import me.murks.jopl.Outlines
import java.io.FileInputStream
import java.io.IOException

class FeedImportActivity : FeedWatcherBaseActivity() {

    private lateinit var outlines: Outlines
    private lateinit var adapter: FeedImportRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_import)

       intent.data?.also { fileUri ->
           try {
               val file = contentResolver.openFileDescriptor(fileUri, "r")
               val stream = FileInputStream(file.fileDescriptor)
               outlines = Jopl.outlines(stream)
               activity_feed_import_list_title.visibility = View.VISIBLE
               activity_feed_import_error.visibility = View.INVISIBLE
               adapter = FeedImportRecyclerViewAdapter(outlines.outlines)
               activity_feed_import_feeds.layoutManager =
                       androidx.recyclerview.widget.LinearLayoutManager(this)
               activity_feed_import_feeds.adapter = adapter
               activity_feed_import_button.isEnabled = true
           }
           catch (ioe: IOException) {
               activity_feed_import_error.visibility = View.VISIBLE
               activity_feed_import_button.isEnabled = false
           }
       }

        activity_feed_import_button.setOnClickListener {
        }
    }
}
