package me.murks.podcastwatcher.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import me.murks.podcastwatcher.R
import me.murks.podcastwatcher.activities.QueriesFragment.OnListFragmentInteractionListener
import me.murks.podcastwatcher.model.Query

class OverviewActivity : AppCompatActivity(), OnListFragmentInteractionListener {

    private lateinit var mDrawerLayout: DrawerLayout

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
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            when(menuItem.itemId) {
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
            }

            true
        }


        mDrawerLayout = findViewById(R.id.drawer_layout)
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

    override fun onListFragmentInteraction(item: Query) {
        val intent = Intent(this, QueryActivity::class.java)
        intent.putExtra(QueryActivity.INTENT_QUERY_EXTRA, item)
        startActivityForResult(intent, 0)
    }

}
