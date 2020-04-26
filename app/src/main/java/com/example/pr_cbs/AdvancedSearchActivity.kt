package com.example.pr_cbs

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_advanced_search.*

import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import android.content.Intent
import android.text.Editable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_event_filter.*
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*


class AdvancedSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_search)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_advanced_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Расширенный поиск"

        var addition = true

        returnValues(this)

        val intent = Intent()
        var searchString = MainActivity.getFromSharedPreferences("search_text", this)

        if (searchString != "" && searchString != null) {

            according_to_search_string.visibility = VISIBLE
            if (searchString.length > 10) searchString = searchString.substring(0..9)

            val text = "Поиск в соотвествии с введенным ранее запросом \"$searchString\""
            according_to_text.text = text
        }

        according_to_button.setOnClickListener {
            addition = false
            according_to_search_string.visibility = GONE
        }

        toolbar.setNavigationOnClickListener {
            container(intent, addition, this)
        }

        advanced_search_activity_button.setOnClickListener {
            container(intent, addition, this)
        }
    }




    private fun returnValues(context: Context){


        val titleFromSP = MainActivity.getFromSharedPreferences("adv_title", context)
        if (titleFromSP != "" && titleFromSP != "null")
            advanced_title.text = titleFromSP.toEditable()


        val authorFromSP = MainActivity.getFromSharedPreferences("adv_author", context)
        if (authorFromSP != "" && authorFromSP != "null")
            advanced_author.text = authorFromSP.toEditable()

        val subjectsFromSP = MainActivity.getFromSharedPreferences("adv_subjects", context)
        if (subjectsFromSP != "" && subjectsFromSP != "null")
            advanced_subjects.text = subjectsFromSP.toEditable()

        val seriesFromSP = MainActivity.getFromSharedPreferences("adv_series", context)
        if (seriesFromSP != "" && seriesFromSP != "null")
            advanced_series.text = seriesFromSP.toEditable()

        val personFromSP = MainActivity.getFromSharedPreferences("adv_person", context)
        if (personFromSP != "" && personFromSP != "null")
            advanced_person.text = personFromSP.toEditable()

        val availableFromSP = MainActivity.getFromSharedPreferences("adv_cb_available", context)
        if (availableFromSP != "" && availableFromSP != "false" ) checkBox_advanced_available.isChecked = true

        val centerFromSP = MainActivity.getFromSharedPreferences("adv_cb_center", context)
        if (centerFromSP != "" && centerFromSP != "false") checkBox_advanced_center.isChecked = true

        val kirovFromSP = MainActivity.getFromSharedPreferences("adv_cb_kirov", context)
        if (kirovFromSP != "" && kirovFromSP != "false") checkBox_advanced_kirov.isChecked = true

        val lavrenevaFromSP = MainActivity.getFromSharedPreferences("adv_cb_lavreneva", context)
        if (lavrenevaFromSP != "" && lavrenevaFromSP != "false") checkBox_advanced_lavreneva.isChecked = true

        val gaidarFromSP = MainActivity.getFromSharedPreferences("adv_cb_gaidar", context)
        if (gaidarFromSP != "" && gaidarFromSP != "false") checkBox_advanced_gaidar.isChecked = true

        val lib2FromSP = MainActivity.getFromSharedPreferences("adv_cb_2lib", context)
        if (lib2FromSP != "" && lib2FromSP != "false") checkBox_advanced_3Lib.isChecked = true

        val lib3FromSP = MainActivity.getFromSharedPreferences("adv_cb_3lib", context)
        if (lib3FromSP != "" && lib3FromSP != "false") checkBox_advanced_2lib.isChecked = true

        val startYearFromSP = MainActivity.getFromSharedPreferences("adv_start_year", context)
        if (startYearFromSP != "" && startYearFromSP != "null") edtFrom.text = startYearFromSP.toEditable()

        val endYearFromSP = MainActivity.getFromSharedPreferences("adv_end_year", context)
        if (endYearFromSP!= "" && endYearFromSP != "null") edtTo.text = endYearFromSP.toEditable()

    }


    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun container(intent: Intent, addition: Boolean, context: Context) {

        MainActivity.putInSharedPreferences("adv_title",checkText(advanced_title), context)
        MainActivity.putInSharedPreferences("adv_author",checkText(advanced_author), context)
        MainActivity.putInSharedPreferences("adv_subjects",checkText(advanced_subjects), context)
        MainActivity.putInSharedPreferences("adv_series",checkText(advanced_series), context)
        MainActivity.putInSharedPreferences("adv_person",checkText(advanced_person), context)

        MainActivity.putInSharedPreferences("adv_addition", addition.toString(), context)

        MainActivity.putInSharedPreferences("adv_cb_available", checkBox_advanced_available.isChecked.toString(), context)
        MainActivity.putInSharedPreferences("adv_cb_center", checkBox_advanced_center.isChecked.toString(), context)
        MainActivity.putInSharedPreferences("adv_cb_kirov", checkBox_advanced_kirov.isChecked.toString(), context)
        MainActivity.putInSharedPreferences("adv_cb_lavreneva",checkBox_advanced_lavreneva.isChecked.toString(), context)
        MainActivity.putInSharedPreferences("adv_cb_gaidar",checkBox_advanced_gaidar.isChecked.toString(), context)
        MainActivity.putInSharedPreferences("adv_cb_2lib",checkBox_advanced_2lib.isChecked.toString(), context)
        MainActivity.putInSharedPreferences("adv_cb_3lib", checkBox_advanced_3Lib.isChecked.toString(), context)

        MainActivity.putInSharedPreferences("adv_start_year", edtFrom.text.toString(), context)
        MainActivity.putInSharedPreferences("adv_end_year", edtTo.text.toString(), context)

        intent.putExtra("title", checkText(advanced_title))
        intent.putExtra("author", checkText(advanced_author))
        intent.putExtra("subjects", checkText(advanced_subjects))
        intent.putExtra("series", checkText(advanced_series))
        intent.putExtra("person", checkText(advanced_person))
        intent.putExtra("addition", addition)

        intent.putExtra("checkBox_available", checkBox_advanced_available.isChecked.toString())
        intent.putExtra("checkBox_centerPushkin", checkBox_advanced_centerPushkin.isChecked.toString())
        intent.putExtra("checkBox_center", checkBox_advanced_center.isChecked.toString())
        intent.putExtra("checkBox_kirov", checkBox_advanced_kirov.isChecked.toString())
        intent.putExtra("checkBox_lavreneva", checkBox_advanced_lavreneva.isChecked.toString())
        intent.putExtra("checkBox_gaidar", checkBox_advanced_gaidar.isChecked.toString())
        intent.putExtra("checkBox_2lib", checkBox_advanced_2lib.isChecked.toString())
        intent.putExtra("checkBox_3lib", checkBox_advanced_3Lib.isChecked.toString())
//            TODO: Ленин
        // intent.putExtra("checkBox_3lib", checkBox_advanced_.isChecked.toString())

        intent.putExtra("date", checkDate(edtFrom, edtTo))
        setResult(1, intent)
        finish()
    }

    private fun checkText(view: EditText): String {
        return if (view.text.toString() == "") "null"
        else view.text.toString()

    }

    private fun checkDate(view1: EditText, view2: EditText): String {
        //val endDate = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        var startDate = 1800
        var endDate = 2020
        var resString = "("

        if ((view1.text.toString() == "") && (view2.text.toString() == "")) return "null"
        else {
            if (view1.text.toString() != "") startDate = view1.text.toString().toInt()
            if (view2.text.toString() != "") endDate = view2.text.toString().toInt()
        }

        if (startDate == 1800 && endDate == 2020) return "null"
        else {
            for (i in startDate..endDate) {
                if (i != endDate)
                    resString += "\"G=$i\"+"
                else resString += "\"G=$i\""
            }
        }
        return resString + ")"
    }


}
