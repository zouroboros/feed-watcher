package me.murks.podcastwatcher.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.murks.podcastwatcher.PodcastWatcherApp
import me.murks.podcastwatcher.R

import me.murks.podcastwatcher.model.Result

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ResultsFragment.OnListFragmentInteractionListener] interface.
 */
class ResultsFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_results_list, container, false)

        val app = PodcastWatcherApp()

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = ResultsRecyclerViewAdapter(app.results, listener)
            }
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

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(result: Result)
    }

}
