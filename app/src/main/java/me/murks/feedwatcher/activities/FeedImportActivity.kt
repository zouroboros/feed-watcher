package me.murks.feedwatcher.activities

import android.content.DialogInterface
import android.os.Bundle
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
               adapter = FeedImportRecyclerViewAdapter(outlines.outlines)
               activity_feed_import_feeds.layoutManager =
                       androidx.recyclerview.widget.LinearLayoutManager(this)
               activity_feed_import_feeds.adapter = adapter
               activity_feed_import_button.isEnabled = true
           }
           catch (ioe: IOException) {
               this.errorDialog(R.string.feed_import_open_opml_failed, ioe.localizedMessage,
                       DialogInterface.OnClickListener { dialog, which -> finish() })
           }
       }

        activity_feed_import_button.setOnClickListener {
            try {
                app.import(adapter.selectedOutlines)
                it.context.openFeeds()
            } catch (e: Exception) {
                it.context.errorDialog(R.string.feed_import_import_failed, e.localizedMessage)
            }
        }
    }
}
