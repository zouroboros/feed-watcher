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
along with FeedWatcher. If not, see <https://www.gnu.org/licenses/>.
Copyright 2019 - 2021 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import me.murks.feedwatcher.R
import me.murks.feedwatcher.databinding.FragmentResultsListItemBinding
import me.murks.feedwatcher.activities.ResultsFragment.OnListFragmentInteractionListener
import me.murks.feedwatcher.HtmlTags
import me.murks.feedwatcher.Texts
import me.murks.feedwatcher.model.Result

/**
 * Adapter for displaying a list of results
 */
class ResultsRecyclerViewAdapter(
        results: List<Result>,
        private val listener: OnListFragmentInteractionListener?)
    : ListRecyclerViewAdapter<ResultsRecyclerViewAdapter.ViewHolder, Result>(results) {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Result
            listener?.onOpenResult(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_results_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = items[position]
        holder.resultName.text = result.item.title
        holder.resultDescription.text = Texts.preview(
                HtmlTags.text(result.item.description), 200, "...")
        holder.resultFeed.text = result.feed.name
        holder.resultDate.text = DateFormat.getDateFormat(holder.mView.context).format(result.found)

        with(holder.mView) {
            tag = result
            setOnClickListener(onClickListener)
        }
    }

    inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        private val binding = FragmentResultsListItemBinding.bind(mView)
        val resultName = binding.resultsResultName
        val resultDescription = binding.resultsResultDescription
        val resultFeed = binding.resultsResultFeed
        val resultDate = binding.resultsResultDate
    }
}
