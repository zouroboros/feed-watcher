package me.murks.podcastwatcher.activities

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.filter_parameter_list_item.view.*
import me.murks.podcastwatcher.R
import me.murks.podcastwatcher.model.FilterParameter

/**
 * @author zouroboros
 * @date 8/24/18.
 */
class FilterParameterRecyclerViewAdapter(val parameter: List<FilterParameter>): RecyclerView.Adapter<FilterParameterRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.filter_parameter_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = parameter[position]

        with(holder.mView) {
            tag = item
        }

        holder.parameterName.text = item.name
        holder.parameterValue.setText(item.stringValue)
    }

    override fun getItemCount(): Int = parameter.count()

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val parameterName = mView.filter_parameter_name!!
        val parameterValue = mView.filter_parameter_value!!
    }
}
