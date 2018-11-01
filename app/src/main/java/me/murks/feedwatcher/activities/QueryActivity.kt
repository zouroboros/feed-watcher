package me.murks.feedwatcher.activities

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.Button
import android.widget.EditText

import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.*

class QueryActivity : FeedWatcherBaseActivity() {

    private lateinit var queryNameText: EditText
    private lateinit var filterList: RecyclerView
    private lateinit var filterAdapter: FilterRecyclerViewAdapter
    private lateinit var addFilterButton: Button
    private lateinit var saveQueryButton: Button
    private var query: Query? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query)

        queryNameText = findViewById(R.id.query_name_edit)
        addFilterButton = findViewById(R.id.query_add_filter_button)
        filterList = findViewById(R.id.query_filter_list)
        saveQueryButton = findViewById(R.id.query_save_button)
        filterList.layoutManager = LinearLayoutManager(this)
        filterList.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL))

        val swipeHelper = ItemTouchHelper(
                object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?,
                                target: RecyclerView.ViewHolder?): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
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

        addFilterButton.setOnClickListener {
            filterAdapter.filter.add(FilterUiModel(FilterType.CONTAINS, app.feeds()))
            filterList.adapter.notifyItemInserted(filterList.adapter.itemCount - 1)
            if(filterList.adapter.itemCount > 0 && !saveQueryButton.isEnabled) {
                saveQueryButton.isEnabled = true
            }
        }

        if(filterList.adapter.itemCount == 0) {
            saveQueryButton.isEnabled = false
        }

        saveQueryButton.setOnClickListener {
            if (query != null) {
                app.updateQuery(
                        Query(query!!.id, queryNameText.text.toString(), filterAdapter.filter()))
            } else {
                app.addQuery(Query(0, queryNameText.text.toString(), filterAdapter.filter()))
            }
            finish()
        }

    }

    companion object {
        const val INTENT_QUERY_EXTRA = "query"
    }
}
