package com.example.pr_cbs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import com.example.pr_cbs.RecordStorage.RecommendedBookStorage.*
import kotlinx.android.synthetic.main.activity_result_main_search.*


class RecommendedBookInfo : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_main_search)


        setSupportActionBar(toolbar_main_search_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)



        val intent = intent


        val book = Instance().getRecordById(intent.getIntExtra("Id", 0))

        main_final_book_author.text = book.author
        main_final_book_title.text = book.title
        main_final_book_publish.text = book.publish
        main_final_book_series.text = book.series
        main_final_book_year.text = book.year
        main_final_book_size.text = book.size
        main_final_book_language.text = book.lang
        main_final_book_isbn.text = book.ISBN
        main_final_book_description.text = book.description
        supportActionBar?.title = "ISBN: " + book.ISBN


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

