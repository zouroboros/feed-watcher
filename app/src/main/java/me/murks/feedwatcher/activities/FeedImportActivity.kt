package me.murks.feedwatcher.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.murks.feedwatcher.R

import kotlinx.android.synthetic.main.activity_feed_import.*

import me.murks.jopl.Jopl
import me.murks.jopl.Outlines
import java.io.FileInputStream

class FeedImportActivity : AppCompatActivity() {

    private lateinit var outlines: Outlines

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_import)

       intent.data?.also { fileUri ->
           val file = contentResolver.openFileDescriptor(fileUri, "r")
           val stream = FileInputStream(file.fileDescriptor)
           outlines = Jopl.outlines(stream)
       }
    }
}
