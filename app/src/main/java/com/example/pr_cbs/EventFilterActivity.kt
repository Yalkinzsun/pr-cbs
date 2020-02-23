package com.example.pr_cbs

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import android.content.Intent
import android.os.Build
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TextView

import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_event_filter.*
import kotlinx.android.synthetic.main.activity_main.view.*

import java.text.SimpleDateFormat
import java.util.*


class EventFilterActivity : AppCompatActivity() {

    lateinit var dpd: DatePickerDialog
    lateinit var final_date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_filter)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_event_filter)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Фильтр"

        val df = SimpleDateFormat("dd.MM.yyyy")
        val date = df.format(Calendar.getInstance().time)
        event_edtFrom.hint = date
        event_edtTo.hint = date

        val c = Calendar.getInstance()
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        val month: Int = c.get(Calendar.MONTH)
        val year: Int = c.get(Calendar.YEAR)



        IV_event_1.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { _, Year, monthOfYear, dayOfMonth ->

                        if (dayOfMonth < 10 || monthOfYear < 11) {
                            if (dayOfMonth < 10 && monthOfYear < 10)
                                final_date = "0$dayOfMonth.0${monthOfYear + 1}.$Year"
                            else if (dayOfMonth < 10)
                                final_date = "0$dayOfMonth.${monthOfYear + 1}.$Year"
                            else if (monthOfYear < 10)
                                final_date = "$dayOfMonth.0${monthOfYear + 1}.$Year"
                        } else
                            final_date = "$dayOfMonth.${monthOfYear + 1}.$Year"

                        event_edtFrom.setText(final_date, TextView.BufferType.EDITABLE)
                    },
                    year,
                    month,
                    day
                )

                dpd.show()
            }

        }

        IV_event_2.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { _, Year, monthOfYear, dayOfMonth ->

                        if (dayOfMonth < 10 || monthOfYear < 11) {
                            if (dayOfMonth < 10 && monthOfYear < 10)
                                final_date = "0$dayOfMonth.0${monthOfYear + 1}.$Year"
                            else if (dayOfMonth < 10)
                                final_date = "0$dayOfMonth.${monthOfYear + 1}.$Year"
                            else if (monthOfYear < 10)
                                final_date = "$dayOfMonth.0${monthOfYear + 1}.$Year"
                        } else
                            final_date = "$dayOfMonth.${monthOfYear + 1}.$Year"

                        event_edtTo.setText(final_date, TextView.BufferType.EDITABLE)
                    },
                    year,
                    month,
                    day
                )

                dpd.show()
            }

        }

        toolbar.setNavigationOnClickListener {


            val intent = Intent()

//            intent.putExtra("title", advanced_title.text.toString())


            setResult(2, intent)


            finish()
            Toast.makeText(this, "Фильтр применён", Toast.LENGTH_LONG).show()
        }


    }


}
