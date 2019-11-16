package com.example.pr_cbs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import android.content.Intent
import com.example.pr_cbs.RecordStorage.BookRecord
import com.example.pr_cbs.RecordStorage.RecordStorage
import com.example.pr_cbs.RecordStorage.RecordStorageFake
import kotlinx.android.synthetic.main.activity_result_main_search.*


class ResultMainSearch : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_main_search)


        setSupportActionBar(toolbar_main_search_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val intent = intent

        val book = RecordStorageFake.Instance().getRecordById(intent.getIntExtra("Id", 0))
        search_res_toolbar.text = book.ISBN
        card_book_name_text_view.text = book.title
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

