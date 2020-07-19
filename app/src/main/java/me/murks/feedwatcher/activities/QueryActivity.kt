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
along with FeedWatcher.  If not, see <https://www.gnu.org/licenses/>.
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.annotation.NonNull
import kotlinx.android.synthetic.main.query_filter_list_item.*

import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.*
import java.util.*

class QueryActivity : FeedWatcherBaseActivity(),
        FilterRecyclerViewAdapter.FilterRecyclerViewAdapterListener {

    private lateinit var queryNameText: EditText
    private lateinit var filterList: RecyclerView
    private lateinit var filterAdapter: FilterRecyclerViewAdapter
    private var query: Query? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query)
        setSupportActionBar(findViewById(R.id.toolbar))

        queryNameText = findViewById(R.id.query_name_edit)
        filterList = findViewById(R.id.query_filter_list)

        queryNameText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                invalidateOptionsMenu()
            }
        })

        filterList.layoutManager = LinearLayoutManager(this)
        filterList.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL))

        val swipeHelper = ItemTouchHelper(
                object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    override fun onMove(recyclerView: RecyclerView,
                                        viewHolder: RecyclerView.ViewHolder,
                                        target: RecyclerView.ViewHolder): Boolean {
                        return false
                    }


                    override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                filterAdapter.filter.removeAt(viewHolder.adapterPosition)
                filterAdapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

        })
        swipeHelper.attachToRecyclerView(filterList)
        filterAdapter = FilterRecyclerViewAdapter(emptyList(), app, this)


        if (intent.hasExtra(INTENT_QUERY_EXTRA)) {
            val queryId = intent.extras!!.getLong(INTENT_QUERY_EXTRA)
            query = app.query(queryId)
            queryNameText.setText(query!!.name)
            filterAdapter = FilterRecyclerViewAdapter(query!!.filter, app, this)
        }

        filterList.adapter = filterAdapter

        filterAdapter.registerAdapterDataObserver(object: androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                invalidateOptionsMenu()
                super.onItemRangeRemoved(positionStart, itemCount)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_query_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val save = menu.findItem(R.id.query_save)
        if(filterAdapter.itemCount < 1 || queryNameText.text.isEmpty()) {
            save.isEnabled = false
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when(item!!.itemId) {
        R.id.query_add_filter -> {
            filterAdapter.filter.add(FilterUiModel(FilterType.CONTAINS, app.feeds()))
            filterAdapter.notifyItemInserted(filterAdapter.itemCount - 1)
            invalidateOptionsMenu()
            true
        }
        R.id.query_save -> {
            if (query != null) {
                app.updateQuery(
                        Query(query!!.id, queryNameText.text.toString(), filterAdapter.filter()))
            } else {
                app.addQuery(Query(0, queryNameText.text.toString(), filterAdapter.filter()))
            }
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun showStartDatePicker(model: FilterUiModel) {
        val dialog = DateTimePickerDialogFragment()
        dialog.listener = object : DateTimePickerDialogFragment.DateTimePickerDialogListener {
            override fun dateTimeSelected(date: Calendar) {
                model.startDate = date.time
                filterAdapter.notifyDataSetChanged()
            }
        }
        dialog.show(supportFragmentManager, "DateTimePickerFragment")
    }

    companion object {
        const val INTENT_QUERY_EXTRA = "query"
    }
}
