package me.murks.podcastwatcher.activities

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import kotlinx.android.synthetic.main.query_filter_list_item.view.*
import me.murks.podcastwatcher.R
import me.murks.podcastwatcher.model.Filter
import me.murks.podcastwatcher.model.FilterType


class FilterRecyclerViewAdapter(
        private val filter: List<Filter>)
    : RecyclerView.Adapter<FilterRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.query_filter_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filter[position]

        with(holder.mView) {
            tag = item
        }
        holder.type.setSelection(FilterType.values().indexOf(item.type))
        holder.parameterList.layoutManager = LinearLayoutManager(holder.mView.context)
        holder.parameterList.adapter = FilterParameterRecyclerViewAdapter(item.parameter)
    }

    override fun getItemCount(): Int = filter.count()

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val type: Spinner = mView.filter_filtertype_spinner
        val typeAdapter = ArrayAdapter.createFromResource(mView.context, R.array.filter_filtertypes,
                android.R.layout.simple_spinner_item)
        val parameterList = mView.filter_parameter_list
        init {
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            type.adapter = typeAdapter
        }
    }
}
