package me.murks.feedwatcher.activities

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import kotlinx.android.synthetic.main.query_filter_list_item.view.*
import me.murks.feedwatcher.BR
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Filter
import me.murks.feedwatcher.model.FilterType
import java.util.*

class FilterRecyclerViewAdapter(filter: List<Filter>)
    : RecyclerView.Adapter<FilterRecyclerViewAdapter.ViewHolder>() {

    val filter = LinkedList(filter.map { FilterUiModel(it) })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.query_filter_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filter[holder.adapterPosition]
        holder.setUiModel(item)
    }

    override fun getItemCount(): Int = filter.count()

    fun filter(): List<Filter> {
        return filter.withIndex().map { it.value.filter(it.index) }
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val type: Spinner = mView.filter_filtertype_spinner
        val typeAdapter = ArrayAdapter.createFromResource(mView.context, R.array.filter_filtertypes,
                android.R.layout.simple_spinner_item)

        val containsFilterPanel = mView.filter_contains_filter
        val containsTextView = mView.filter_contains_filter_text
        val feedFilterPanel = mView.filter_feed_filter

        val binding: ViewDataBinding = DataBindingUtil.bind(mView)!!

        private lateinit var uiModel: FilterUiModel

        init {
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            type.adapter = typeAdapter
            type.onItemSelectedListener = (object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val newType = FilterType.values()[p2]
                    uiModel.type = newType
                    showFilterPanel(newType)
                }
            })
        }

        private fun filterPanel(): List<View> {
            return listOf(containsFilterPanel, feedFilterPanel);
        }

        private fun hideFilterPanel() {
            for (panel in filterPanel()) {
                panel.visibility = View.GONE
            }
        }

        private fun showFilterPanel(filterType: FilterType) {
            hideFilterPanel()
            if(filterType == FilterType.CONTAINS) {
                containsFilterPanel.visibility = View.VISIBLE

            } else if(filterType == FilterType.FEED) {
                feedFilterPanel.visibility = View.VISIBLE
            }
        }

        fun setUiModel(model: FilterUiModel) {
            uiModel = model
            binding.setVariable(BR.model, uiModel)
            binding.executePendingBindings()
            showFilterPanel(uiModel.type)
            containsTextView.setText(uiModel.containsText)
        }
    }
}
