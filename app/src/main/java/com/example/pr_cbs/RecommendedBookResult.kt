package com.example.pr_cbs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.recommended_book_result.*


class RecommendedBookResult : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recommended_book_result)


        setSupportActionBar(toolbar_recommended_book)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Конкретная книга"


    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

