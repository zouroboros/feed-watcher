package me.murks.feedwatcher.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Xml
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.preferences_activity.*
import me.murks.feedwatcher.R
import me.murks.feedwatcher.tasks.ActionTask
import me.murks.feedwatcher.tasks.ErrorHandlingTaskListener
import me.murks.feedwatcher.tasks.Tasks
import me.murks.jopl.OpWriter
import java.io.FileOutputStream
import java.io.FileWriter
import java.lang.Exception

class PreferencesActivity : FeedWatcherBaseActivity() {

    companion object {
        const val DATABASE_EXPORT_SELECT_FILE_REQUEST_CODE = 1234;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        open_about_activity_button.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        export_database_button.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_CREATE_DOCUMENT
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent,
                    resources.getString(R.string.export_database)),
                    DATABASE_EXPORT_SELECT_FILE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DATABASE_EXPORT_SELECT_FILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val selectedFile = data!!.data!!
                Tasks.run<Unit, Unit>({
                    val file = contentResolver.openFileDescriptor(selectedFile, "w")
                    FileOutputStream(file!!.fileDescriptor).use {
                        app.exportDatabase(it)
                    }
                }, {
                    Toast.makeText(this, R.string.database_successfully_exported, Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(this, R.string.database_export_failed, Toast.LENGTH_SHORT).show()
                    app.environment.log.error("Database export to ${selectedFile.path} failed.", it)
                }).execute(Unit)

            }
        }
    }
}