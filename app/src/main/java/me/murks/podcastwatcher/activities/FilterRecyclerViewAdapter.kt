package me.murks.podcastwatcher.activities

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import kotlinx.android.synthetic.main.query_filter_list_item.view.*
import me.murks.podcastwatcher.R
import me.murks.podcastwatcher.model.FilterModels
import me.murks.podcastwatcher.model.Filter
import me.murks.podcastwatcher.model.FilterParameter
import me.murks.podcastwatcher.model.FilterType

class FilterRecyclerViewAdapter(filter: List<Filter>)
    : RecyclerView.Adapter<FilterRecyclerViewAdapter.ViewHolder>() {

    val filter = ArrayList(filter)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.query_filter_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filter[holder.adapterPosition]

        with(holder.mView) {
            tag = item
        }

        holder.type.setSelection(FilterType.values().indexOf(item.type))
        holder.parameterList.layoutManager = LinearLayoutManager(holder.mView.context)

        val parameter = if (item.parameter.isEmpty())
            FilterModels.defaultParameter(item.type) else item.parameter

        holder.parameterList.adapter = FilterParameterRecyclerViewAdapter(parameter)

        holder.type.onItemSelectedListener = (object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val newType = FilterType.values()[p2]
                if (newType != item.type) {
                    val parameter = FilterModels.defaultParameter(newType)
                    holder.parameterList.adapter = FilterParameterRecyclerViewAdapter(parameter)
                    filter[holder.adapterPosition] = Filter(newType, parameter, holder.adapterPosition)
                    notifyItemChanged(holder.adapterPosition)
                }
            }
        })

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
