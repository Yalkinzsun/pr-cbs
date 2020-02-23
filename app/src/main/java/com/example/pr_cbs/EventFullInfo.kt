package com.example.pr_cbs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pr_cbs.RecordStorage.EventStorage
import kotlinx.android.synthetic.main.activity_event_full_info.*
import kotlinx.android.synthetic.main.adapter_event_item.*


class EventFullInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_full_info)


        setSupportActionBar(toolbar_event_full)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val intent = intent

        val event = EventStorage.Instance().getRecordById(intent.getIntExtra("event_id", 0))

        full_event_additional_information.text = event.additional_information
        full_event_address.text = event.address
        full_event_annotation.text = event.annotation
        full_event_cover.setBackgroundResource(R.drawable.cover_event_1)
        full_event_date.text = event.start_date
        full_event_time.text = event.start_time + " - " + event.end_time
        full_event_library.text = event.library
        full_event_responsible_person.text = event.responsible_person
        full_event_phone_number.text = event.phone_number
        full_event_name.text = event.name
        supportActionBar?.title = event.start_date


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
