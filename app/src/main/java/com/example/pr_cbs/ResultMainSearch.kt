package com.example.pr_cbs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import android.content.Intent
import kotlinx.android.synthetic.main.activity_result_main_search.*


class ResultMainSearch : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_main_search)


        setSupportActionBar(toolbar_main_search_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val intent = intent

        search_res_toolbar.text = intent.getStringExtra("msn")




    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

