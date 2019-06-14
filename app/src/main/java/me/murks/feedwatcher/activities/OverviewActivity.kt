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
along with FeedWatcher. If not, see <https://www.gnu.org/licenses/>.
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import me.murks.feedwatcher.R
import me.murks.feedwatcher.model.Query
import me.murks.feedwatcher.model.Result
import java.lang.IllegalArgumentException

class OverviewActivity : FeedWatcherBaseActivity(), QueriesFragment.OnListFragmentInteractionListener,
    ResultsFragment.OnListFragmentInteractionListener {

    private lateinit var mDrawerLayout: DrawerLayout
    private var currentFragment = R.id.nav_queries
    private lateinit var navigationView: NavigationView

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

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            mDrawerLayout.closeDrawers()

            navigateTo(menuItem.itemId)

            true
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(
                object: FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                        super.onFragmentStarted(fm, f)
                        navigationView.setCheckedItem(when(f) {
                            is FeedsFragment -> R.id.nav_feeds
                            is QueriesFragment -> R.id.nav_queries
                            is ResultsFragment -> R.id.nav_results
                            is PreferencesFragment -> R.id.nav_preferences
                            else -> throw IllegalArgumentException("Unexpected fragment: ${f}")
                        })
                    }
        }, false)


        mDrawerLayout = findViewById(R.id.drawer_layout)

        val gotoFragment = savedInstanceState?.getInt(CURRENT_FRAGMENT) ?:
            intent?.getIntExtra(CURRENT_FRAGMENT, currentFragment)!!

        openFragment(gotoFragment, false)
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
        intent.putExtra(QueryActivity.INTENT_QUERY_EXTRA, item.id)
        startActivity(intent)
    }

    override fun onOpenResult(result: Result) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.RESULT_EXTRA_NAME, result.id)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_FRAGMENT, currentFragment)
    }

    private fun navigateTo(navId: Int) {
        navigationView.setCheckedItem(navId)
        when(navId) {
            R.id.nav_add_query -> {
                val intent = Intent(this, QueryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_add_feed -> {
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
            }
            else -> {
                openFragment(navId, true)
            }
        }
    }

    private fun openFragment(navId: Int, allowBack: Boolean) {
        navigationView.setCheckedItem(navId)
        currentFragment = navId
        val transaction = supportFragmentManager.beginTransaction()
        when(navId) {
            R.id.nav_feeds -> {
                transaction.replace(R.id.overview_fragment_container, FeedsFragment())
            }
            R.id.nav_queries -> {
                transaction.replace(R.id.overview_fragment_container, QueriesFragment())
            }
            R.id.nav_results -> {
                transaction.replace(R.id.overview_fragment_container, ResultsFragment())
            }
            R.id.nav_preferences -> {
                transaction.replace(R.id.overview_fragment_container, PreferencesFragment())
            }
        }
        if (allowBack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    companion object {
        const val CURRENT_FRAGMENT = "current_fragment"
    }
}
