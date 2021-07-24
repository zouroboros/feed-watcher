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
Copyright 2019 - 2021 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.graphics.Paint
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import me.murks.feedwatcher.databinding.QueryFilterListItemBinding
import me.murks.feedwatcher.BR
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Filter
import me.murks.feedwatcher.model.FilterType
import java.util.*

class FilterRecyclerViewAdapter(filter: List<Filter>, app: FeedWatcherApp,
                                val listener: FilterRecyclerViewAdapterListener)
    : RecyclerView.Adapter<FilterRecyclerViewAdapter.ViewHolder>() {

    private val feeds = app.feeds()
    val filter: MutableList<FilterUiModel> = LinkedList(filter.map { FilterUiModel(it, feeds) })

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
        private val viewBinding = QueryFilterListItemBinding.bind(mView)
        val type: Spinner = viewBinding.filterFiltertypeSpinner
        val typeAdapter = ArrayAdapter.createFromResource(mView.context, R.array.filter_filtertypes,
                android.R.layout.simple_spinner_item)

        val spinner = viewBinding.filterFeedFilterFeed

        val containsFilterPanel = viewBinding.filterContainsFilter
        val feedFilterPanel = viewBinding.filterFeedFilter
        val newFilterPanel = viewBinding.filterNewFilter

        val startDate = viewBinding.filterNewStartDate

        val binding: ViewDataBinding = DataBindingUtil.bind(mView)!!

        private lateinit var uiModel: FilterUiModel

        init {
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            type.adapter = typeAdapter
            startDate.paintFlags = startDate.paintFlags.or(Paint.UNDERLINE_TEXT_FLAG)
        }

        private fun filterPanel(): List<View> {
            return listOf(containsFilterPanel, feedFilterPanel, newFilterPanel)
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
            } else if(filterType == FilterType.NEW) {
                newFilterPanel.visibility = View.VISIBLE
            }
        }

        fun setUiModel(model: FilterUiModel) {
            uiModel = model
            spinner.adapter = ArrayAdapter(mView.context,
                    android.R.layout.simple_spinner_dropdown_item,
                    uiModel.feedNames)
            binding.setVariable(BR.model, uiModel)
            binding.executePendingBindings()

            type.onItemSelectedListener = (object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val newType = FilterType.values()[p2]
                    uiModel.type = newType
                    showFilterPanel(newType)
                }
            })

            showFilterPanel(uiModel.type)
            newFilterPanel.setOnClickListener { listener.showStartDatePicker(uiModel) }
        }
    }

    interface FilterRecyclerViewAdapterListener {
        fun showStartDatePicker(model: FilterUiModel)
    }
}
