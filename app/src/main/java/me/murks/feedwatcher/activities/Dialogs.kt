package me.murks.feedwatcher.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import me.murks.feedwatcher.R

/**
 * Shows an error message to the user
 * @author zouroboros
 */
fun Context.errorDialog(errorResourceId: Int, additionalInfos: String, listener: DialogInterface.OnClickListener? = null){
    val builder = AlertDialog.Builder(this)
    builder.setMessage(this.resources.getString(errorResourceId, additionalInfos))
    builder.setPositiveButton(R.string.error_dialog_ok_button) { dialog, which ->
        dialog.cancel()
        listener?.onClick(dialog, which)
    }
    builder.create().show()
}