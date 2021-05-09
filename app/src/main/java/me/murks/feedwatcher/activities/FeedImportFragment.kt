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
Copyright 2021 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import me.murks.feedwatcher.R
import me.murks.feedwatcher.databinding.FragmentFeedImportBinding

class FeedImportFragment : Fragment() {
    private lateinit var binding: FragmentFeedImportBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedImportBinding.bind(view)

        binding.feedImportFragmentButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            intent.type = "*/*"
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
