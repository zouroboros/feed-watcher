package me.murks.feedwatcher.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import me.murks.feedwatcher.FeedWatcherApp
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Query
import me.murks.feedwatcher.model.Result

class OverviewActivity : FeedWatcherBaseActivity(), QueriesFragment.OnListFragmentInteractionListener,
    ResultsFragment.OnListFragmentInteractionListener {

    private lateinit var mDrawerLayout: DrawerLayout
    private var currentFragment = R.id.nav_queries

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_overview)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            mDrawerLayout.closeDrawers()

            openFragment(menuItem.itemId)

            true
        }


        mDrawerLayout = findViewById(R.id.drawer_layout)

        val gotoFragment = savedInstanceState?.getInt(CURRENT_FRAGMENT) ?:
            intent?.getIntExtra(CURRENT_FRAGMENT, currentFragment)!!

        openFragment(gotoFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onOpenQuery(item: Query) {
        val intent = Intent(this, QueryActivity::class.java)
        intent.putExtra(QueryActivity.INTENT_QUERY_EXTRA, item)
        startActivity(intent)
    }

    override fun onOpenResult(result: Result) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.RESULT_EXTRA_NAME, result)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_FRAGMENT, currentFragment)
    }

    private fun openFragment(navId: Int) {
        currentFragment = navId
        when(navId) {
            R.id.nav_feeds -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.overview_fragment_container, FeedsFragment())
                        .addToBackStack(null)
                transaction.commit()
            }
            R.id.nav_queries -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.overview_fragment_container, QueriesFragment())
                        .addToBackStack(null)
                transaction.commit()
            }
            R.id.nav_add_query -> {
                val intent = Intent(this, QueryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_add_feed -> {
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_results -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.overview_fragment_container, ResultsFragment())
                        .addToBackStack(null)
                transaction.commit()
            }
        }
    }

    companion object {
        const val CURRENT_FRAGMENT = "current_fragment"
    }
}
