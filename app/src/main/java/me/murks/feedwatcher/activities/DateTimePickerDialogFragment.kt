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
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import me.murks.feedwatcher.R
import java.util.*

/**
 * @author zouroboros
 */
class DateTimePickerDialogFragment: DialogFragment() {

    var listener: DateTimePickerDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.layout_date_time_picker, null)
        val datePicker = view.findViewById<DatePicker>(R.id.date_time_date_picker)
        val timePicker = view.findViewById<TimePicker>(R.id.date_time_time_picker)
        timePicker.visibility = View.GONE

        val date = Calendar.getInstance()
        datePicker.init(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)) { v, _, _, _ ->
                    v.visibility = View.GONE
                    timePicker.visibility = View.VISIBLE
                }

        val builder = AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton(R.string.date_time_confirm, DialogInterface.OnClickListener
                { _, _ ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(datePicker.year, datePicker.month, datePicker.dayOfMonth,
                            timePicker.hour, timePicker.minute)
                    listener?.dateTimeSelected(selectedDate)
                })
            .setNegativeButton(R.string.date_time_cancel, DialogInterface.OnClickListener
                { _, _ -> })
        return builder.create()
    }

    interface DateTimePickerDialogListener {
        fun dateTimeSelected(date: Calendar)
    }
}