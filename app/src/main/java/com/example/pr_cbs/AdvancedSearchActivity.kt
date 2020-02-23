package com.example.pr_cbs
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_advanced_search.*

import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import android.content.Intent

import androidx.fragment.app.Fragment
import com.example.pr_cbs.Fragments.SearchFragmentv2


class AdvancedSearchActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_search)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_advanced_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {


            val intent = Intent()

            intent.putExtra("title", advanced_title.text.toString())
            intent.putExtra("author", advanced_author.text.toString())
//            intent.putExtra("subjects", advanced_subjects.text.toString())
//            intent.putExtra("series", advanced_series.text.toString())
//            intent.putExtra("person", advanced_person.text.toString())
//            intent.putExtra("author", advanced_author.text.toString())
//
//            intent.putExtra("checkBox_available", checkBox_advanced_available.isChecked)
//            intent.putExtra("checkBox_centerPushkin", checkBox_advanced_centerPushkin.isChecked)
//            intent.putExtra("checkBox_center", checkBox_advanced_center.isChecked)
//            intent.putExtra("checkBox_kirov", checkBox_advanced_kirov.isChecked)
//            intent.putExtra("checkBox_lavreneva",  checkBox_advanced_lavreneva.isChecked)
//            intent.putExtra("checkBox_2lib", checkBox_advanced_2lib.isChecked)
//            intent.putExtra("checkBox_advanced_3Lib", checkBox_advanced_3Lib.isChecked)
//
//            intent.putExtra("dateFrom",  edtFrom.text.toString().toInt())
//            intent.putExtra("dateTo",  edtTo.text.toString().toInt())

            setResult(1, intent)


            finish()
            Toast.makeText(this, "Фильтр применён", Toast.LENGTH_LONG).show()
        }


    }


}
