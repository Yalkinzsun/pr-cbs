package com.example.pr_cbs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about_notification.*

class AboutNotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_notification)

        setSupportActionBar(toolbar_about_notification)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = "Уведомления"

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
