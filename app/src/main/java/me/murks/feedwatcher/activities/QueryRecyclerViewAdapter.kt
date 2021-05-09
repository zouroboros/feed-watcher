package me.murks.feedwatcher.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import me.murks.feedwatcher.R
import me.murks.feedwatcher.databinding.FragmentQueriesListItemBinding

import me.murks.feedwatcher.activities.QueriesFragment.OnListFragmentInteractionListener

import me.murks.feedwatcher.model.Query

class QueryRecyclerViewAdapter(queries: List<Query>,
                               private val listener: OnListFragmentInteractionListener?)
    : ListRecyclerViewAdapter<QueryRecyclerViewAdapter.ViewHolder, Query>(queries) {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Query
            listener?.onOpenQuery(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_queries_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name

        with(holder.mView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val binding = FragmentQueriesListItemBinding.bind(mView)
        val name: TextView = binding.queryName

        override fun toString(): String {
            return super.toString() + " '" + name.text + "'"
        }
    }
}
