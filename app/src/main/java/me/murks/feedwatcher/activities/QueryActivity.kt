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

import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.*

class QueryActivity : FeedWatcherBaseActivity() {

    private lateinit var queryNameText: EditText
    private lateinit var filterList: androidx.recyclerview.widget.RecyclerView
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

        filterList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        filterList.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))

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
        filterAdapter = FilterRecyclerViewAdapter(emptyList(), app)


        if (intent.hasExtra(INTENT_QUERY_EXTRA)) {
            val queryId = intent.extras.getLong(INTENT_QUERY_EXTRA)
            query = app.query(queryId)
            queryNameText.setText(query!!.name)
            filterAdapter = FilterRecyclerViewAdapter(query!!.filter, app)
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

    companion object {
        const val INTENT_QUERY_EXTRA = "query"
    }
}
