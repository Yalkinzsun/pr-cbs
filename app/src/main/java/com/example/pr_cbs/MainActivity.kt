package com.example.pr_cbs

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.inputmethod.EditorInfo
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pr_cbs.Fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import com.google.android.material.appbar.AppBarLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.View
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity() {

    lateinit var searchFragment: SearchFragmentv2
    var isPressed = false
    var keyboardSearchIconPressed = false

    fun getSearchText(): String {
        return toolbar.input_line.text.toString()
    }

    fun setSearchText(mCombination: String): String {

        toolbar.input_line.text = mCombination.toEditable()
        return "true"
    }

    fun isSearchIconPressed(): Boolean {
        return isPressed
    }

    fun isKeyboardSearchIconPressed(): Boolean {
        return keyboardSearchIconPressed
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private val onNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                loadFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_catalog -> {
                loadFragment(SearchFragmentv2())

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        loadFragment(HomeFragment())

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.imageViewSearch.setOnClickListener {
            navView.selectedItemId = R.id.navigation_catalog
            isPressed = true

        }


        input_line.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    navView.selectedItemId = R.id.navigation_catalog
                    keyboardSearchIconPressed = true
                    // Скрытие клавиатуры
                    val view = this.currentFocus
                    view?.let { v ->
                        val imm =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.hideSoftInputFromWindow(v.windowToken, 0)
                    }

                    true
                }
                else -> false
            }
        }

    }


    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
