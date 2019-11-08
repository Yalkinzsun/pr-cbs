package com.example.pr_cbs

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pr_cbs.Fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    fun getSearchText(): String {
        return toolbar.editText.text.toString()
    }

    private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                loadFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_catalog -> {
                loadFragment(SearchFragmentv2( this::getSearchText ))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_event -> {
                loadFragment(EventFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_to_reader -> {
                loadFragment(ToReaderFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_more -> {
                loadFragment(MoreFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

//    override fun onCreateOptionsMenu(menu: Menu):Boolean {
//        menuInflater.inflate(R.menu.top_action_menu, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem):Boolean {
//
//        when (item.itemId){
//            R.id.menu_top_about -> {
//               val intent = Intent(this, AboutActivity::class.java)
//               startActivity(intent)
//            }
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        imageViewSearch.setOnClickListener {
            val intent = Intent(this, BookSearchResult1::class.java)
            startActivity(intent)
        }

        loadFragment(HomeFragment())




        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }


    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
