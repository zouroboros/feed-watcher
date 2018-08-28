package me.murks.podcastwatcher.activities

import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.murks.podcastwatcher.R


import me.murks.podcastwatcher.activities.ResultsFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_results_list_item.view.*
import me.murks.podcastwatcher.model.Result

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class ResultsRecyclerViewAdapter(
        private val results: List<Result>,
        private val listener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<ResultsRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Result
            listener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_results_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.resultName.text = result.name
        holder.resultDescription.text = result.description
        holder.resultFeed.text = result.feedName
        holder.resultDate.text = DateFormat.getDateFormat(holder.mView.context).format(result.found)

        with(holder.mView) {
            tag = result
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = results.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val resultName = mView.results_result_name
        val resultDescription = mView.results_result_description
        val resultFeed = mView.results_result_feed
        val resultDate = mView.results_result_date
    }
}
