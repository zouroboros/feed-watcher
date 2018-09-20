package me.murks.feedwatcher.activities

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.filter_parameter_list_item.view.*
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.FilterParameter

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
        holder.parameterValue.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                parameter[holder.adapterPosition].stringValue = p0.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    override fun getItemCount(): Int = parameter.count()

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val parameterName = mView.filter_parameter_name!!
        val parameterValue = mView.filter_parameter_value!!
    }
}
