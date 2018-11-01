package me.murks.feedwatcher.activities

import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.murks.feedwatcher.R


import me.murks.feedwatcher.activities.ResultsFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_results_list_item.view.*
import me.murks.feedwatcher.HtmlTags
import me.murks.feedwatcher.model.Result
import org.jsoup.Jsoup

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
        holder.resultDescription.text = HtmlTags.text(result.item.description)
        holder.resultFeed.text = result.feed.name
        holder.resultDate.text = DateFormat.getDateFormat(holder.mView.context).format(result.found)

        with(holder.mView) {
            tag = result
            setOnClickListener(onClickListener)
        }
    }

    inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val resultName = mView.results_result_name!!
        val resultDescription = mView.results_result_description!!
        val resultFeed = mView.results_result_feed!!
        val resultDate = mView.results_result_date!!
    }
}
