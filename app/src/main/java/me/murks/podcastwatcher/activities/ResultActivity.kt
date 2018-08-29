package me.murks.podcastwatcher.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TextView
import me.murks.podcastwatcher.R
import me.murks.podcastwatcher.model.Result

class ResultActivity : AppCompatActivity() {

    private lateinit var resultName: TextView
    private lateinit var resultDescription: TextView
    private lateinit var resultFeed: TextView
    private lateinit var resultDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        resultName = findViewById(R.id.result_result_name)
        resultDescription = findViewById(R.id.result_result_description)
        resultFeed = findViewById(R.id.result_result_feed)
        resultDate = findViewById(R.id.result_result_date)

        val intent = getIntent()
        val result = intent.getParcelableExtra<Result>(RESULT_EXTRA_NAME)

        resultName.text = result.item.title
        resultDescription.text = result.item.description
        resultFeed.text = result.feedName
        resultDate.text = DateFormat.getDateFormat(this).format(result.found)
    }

    companion object {
        const val RESULT_EXTRA_NAME = "result"
    }
}
