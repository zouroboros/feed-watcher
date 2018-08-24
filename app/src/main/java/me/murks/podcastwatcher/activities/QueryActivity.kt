package me.murks.podcastwatcher.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import me.murks.podcastwatcher.R
import me.murks.podcastwatcher.model.Query

class QueryActivity : AppCompatActivity() {

    private lateinit var queryNameText: EditText
    private lateinit var filterList: RecyclerView
    private lateinit var filterAdapter: FilterRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query)

        queryNameText = findViewById(R.id.query_name_edit)
        filterList = findViewById(R.id.query_filter_list)
        filterList.layoutManager = LinearLayoutManager(this)
        filterAdapter = FilterRecyclerViewAdapter(emptyList())


        if (intent.hasExtra(INTENT_QUERY_EXTRA)) {
            val query = intent.getParcelableExtra<Query>(INTENT_QUERY_EXTRA);
            queryNameText.setText(query.name)
            filterAdapter = FilterRecyclerViewAdapter(query.filter)
        }

        filterList.adapter = filterAdapter
        filterList.adapter.notifyDataSetChanged()
    }

    companion object {
        const val INTENT_QUERY_EXTRA = "query"
    }
}
