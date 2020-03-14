package me.murks.feedwatcher.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import me.murks.feedwatcher.R
import kotlinx.android.synthetic.main.fragment_feed_import.*

class FeedImportFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feed_import_fragment_button.setOnClickListener {
            val intent = Intent()
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT)
            intent.setType("*/*")
            startActivityForResult(Intent.createChooser(intent,
                    resources.getString(R.string.feed_import_chooser_title)), OverviewActivity.FEED_IMPORT_SELECT_FILE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OverviewActivity.FEED_IMPORT_SELECT_FILE_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                val selectedFile = data?.data
                val intent = Intent(context, FeedImportActivity::class.java)
                intent.data = selectedFile
                startActivity(intent)
            }
        }
    }
}
