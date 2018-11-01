package me.murks.feedwatcher.activities

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.R

import me.murks.feedwatcher.model.Result

// TODO implement result deletion
/**
 * Fragment representing a list of results. Activities that show
 * this fragment must implement the
 * [ResultsFragment.OnListFragmentInteractionListener] interface.
 */
class ResultsFragment : FeedWatcherBaseFragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var adapter: ResultsRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_results_list, container, false)

        adapter = ResultsRecyclerViewAdapter(app.results(), listener)

        if (view is androidx.recyclerview.widget.RecyclerView) {
            view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            view.adapter = adapter
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()
        adapter.items = app.results()
    }

    interface OnListFragmentInteractionListener {
        fun onOpenResult(result: Result)
    }

}
