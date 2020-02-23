package com.example.pr_cbs

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.Adapters.TakenBooksAdapter
import kotlinx.android.synthetic.main.activity_taken_books.*

class TakenBooksActivity : AppCompatActivity() {

    lateinit var sPref: SharedPreferences
    // имя файла настроек
    private val APP_PREFERENCES = "pref_settings"
    private val APP_PREFERENCES_LIBRARY_CARD = "library_card"
    private lateinit var libraryCard: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_taken_books)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_taken_books_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Читаемые книги"


        val takenBooksAdapter = TakenBooksAdapter(this)
        RV_taken_books.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        RV_taken_books.adapter = takenBooksAdapter


        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        if (sPref.contains(APP_PREFERENCES_LIBRARY_CARD) && sPref.getString(
                APP_PREFERENCES_LIBRARY_CARD, ""
            ).toString() != "0")
        {
            libraryCard = sPref.getString(APP_PREFERENCES_LIBRARY_CARD, "").toString()

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}