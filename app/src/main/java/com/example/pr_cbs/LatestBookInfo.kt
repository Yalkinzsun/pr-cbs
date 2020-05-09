package com.example.pr_cbs

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


import com.example.pr_cbs.RecordStorage.RecommendedBooksStorage.*
import kotlinx.android.synthetic.main.activity_result_main_search.*


class LatestBookInfo : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_main_search)


        setSupportActionBar(toolbar_main_search_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        main_search_availability_info_block.visibility = View.GONE
        val intent = intent


        val book = Instance().getRecordById(intent.getIntExtra("Id", 0))

        if (book.author != "null null") main_final_book_author.text = book.author
        else main_final_book_author.visibility = View.GONE

        main_final_book_title.text = book.title

        if (book.publish != "null (null)") main_final_book_publish.text = book.publish
        else {
            main_final_book_publish.visibility = View.GONE
            main_final_book_publish_text.visibility = View.GONE
        }

        if (book.series != null) main_final_book_series.text = book.series
        else {
            main_final_book_series.visibility = View.GONE
            main_final_book_series_text.visibility = View.GONE
        }


        if (book.year != "null" && book.year != null) main_final_book_year.text = book.year
        else main_final_book_year_block.visibility = View.GONE


        if (book.size != "null" && book.size != null) main_final_book_size.text = book.size
        else main_final_book_size_block.visibility = View.GONE

        if (book.lang != "null" && book.lang != null) main_final_book_language.text = book.lang
        else main_final_book_language_block.visibility = View.GONE

        if (book.ISBN != "null" && book.ISBN != null)  main_final_book_isbn.text = book.ISBN
        else main_final_book_isbn_block.visibility = View.GONE

        main_final_book_description.text = book.description

        if (book.link != null && book.link != "nullnull") Glide.with(this).load(book.link).into(main_search_result_book_cover)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

