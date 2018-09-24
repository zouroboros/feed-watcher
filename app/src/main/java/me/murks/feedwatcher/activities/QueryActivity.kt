package me.murks.feedwatcher.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import me.murks.feedwatcher.FeedWatcherApp

import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Filter
import me.murks.feedwatcher.model.FilterModels
import me.murks.feedwatcher.model.FilterType
import me.murks.feedwatcher.model.Query

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
        filterAdapter = FilterRecyclerViewAdapter(emptyList())


        if (intent.hasExtra(INTENT_QUERY_EXTRA)) {
            query = intent.getParcelableExtra(INTENT_QUERY_EXTRA);
            queryNameText.setText(query!!.name)
            filterAdapter = FilterRecyclerViewAdapter(query!!.filter)
        }

        filterList.adapter = filterAdapter

        addFilterButton.setOnClickListener { filterAdapter.filter.add(Filter(FilterType.CONTAINS,
                FilterModels.defaultParameter(FilterType.CONTAINS), filterAdapter.itemCount))
            filterList.adapter.notifyItemInserted(filterList.adapter.itemCount - 1)
        }

        saveQueryButton.setOnClickListener {
            if (query != null) {
                app.updateQuery(
                        Query(query!!.id, queryNameText.text.toString(), filterAdapter.filter))
            } else {
                app.addQuery(Query(0, queryNameText.text.toString(), filterAdapter.filter))
            }
            finish()
        }

    }

    companion object {
        const val INTENT_QUERY_EXTRA = "query"
    }
}
