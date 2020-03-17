package com.example.pr_cbs

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pr_cbs.RecordStorage.EventStorage
import com.example.pr_cbs.Reminder.ReminderBroadcast
import kotlinx.android.synthetic.main.activity_event_full_info.*
import java.util.*


class EventFullInfo : AppCompatActivity() {

    lateinit var id: String

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


        id = UUID.randomUUID().toString()


        createNotificationChannel(id, event.start_date)

        event_full_CL_tv_1.setOnClickListener {

            val intent2 = Intent(this, ReminderBroadcast::class.java)
                .putExtra("test", event.name)
                .putExtra("id", id)


            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent2, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val timeA: Long = System.currentTimeMillis()
            val tenSecondsInMillis: Long = 1000 * 10

            alarmManager.set(AlarmManager.RTC_WAKEUP, timeA + tenSecondsInMillis, pendingIntent)
        }


        event_full_CL_tv_2.setOnClickListener {
            val mapIntent = Intent(this, MapActivity::class.java)
            startActivity(mapIntent)
        }


        event_full_CL_iv_info.setOnClickListener {
            val aboutNotificationIntent = Intent(this, AboutNotificationActivity::class.java)
            startActivity(aboutNotificationIntent)
        }
    }



    private fun createNotificationChannel(id: String, name: CharSequence) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//            val name: CharSequence = "LemubitReminderChannel"

            val description =  "Channel for ReminderBroadcast $id"


            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance)
            channel.description = description


            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)


        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
