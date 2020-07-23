package com.example.pr_cbs

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.pr_cbs.RecordStorage.EventStorage
import com.example.pr_cbs.Reminder.ReminderBroadcast
import kotlinx.android.synthetic.main.activity_event_full_info.*


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
        full_event_date.text = event.start_date
        val time = event.start_time + " - " + event.end_time
        full_event_time.text = time
        full_event_library.text = event.library
        full_event_responsible_person.text = event.responsible_person
        full_event_phone_number.text = event.phone_number
        full_event_name.text = event.name
        supportActionBar?.title = event.start_date


        event_full_CL_tv_1.setOnClickListener {


            val builder = AlertDialog.Builder(this@EventFullInfo)

            val id = (1..100).random()
            
            // Заголовок AlertDialog
            builder.setTitle("Напомнить?")
            // Текст AlertDialog
            builder.setMessage("$id")

            val timeA: Long = System.currentTimeMillis()
            val tenSecondsInMillis: Long = 1000 * 10

            builder.setPositiveButton("YES") { dialog, which ->

                getNotificationBuilder(this@EventFullInfo).apply {
                    // Обязательные параметры, без которые Notification не будет работать
                    setSmallIcon(android.R.mipmap.sym_def_app_icon)
                    setContentTitle(event.name)
                    setContentText(event.additional_information)
                  //  setWhen(timeA + tenSecondsInMillis)
                }.build().also { getNotificationManager(this@EventFullInfo).notify(id, it) }



//                val intent2 = Intent(this, ReminderBroadcast::class.java)
//                    .putExtra("event_name", event.name)
//                    .putExtra("event_text", event.additional_information)
//                    .putExtra("id", id)
//
//                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent2, 0)
//                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//                val timeA: Long = System.currentTimeMillis()
//                val tenSecondsInMillis: Long = 100 * 10
//
//                alarmManager.set(AlarmManager.RTC_WAKEUP, timeA + tenSecondsInMillis, pendingIntent)

                Toast.makeText(applicationContext, "Уведомление установлено!", Toast.LENGTH_SHORT)
                    .show()

            }

            // Установка текста кнопки отмены в диалоге и обработчика по нажатию
            builder.setNeutralButton("Cancel") { _, _ ->
            }

            // Создание настроенного экземпляра AlertDialog
            val dialog: AlertDialog = builder.create()

            // Вывод на экран созданного AlertDialog
            dialog.show()


        }


//        event_full_CL_tv_2.setOnClickListener {
//            val mapIntent = Intent(this, MapActivity::class.java)
//            startActivity(mapIntent)
//        }


//        event_full_CL_iv_info.setOnClickListener {
//            val aboutNotificationIntent = Intent(this, AboutNotificationActivity::class.java)
//            startActivity(aboutNotificationIntent)
//        }
    }


    fun getNotificationBuilder(context: Context): NotificationCompat.Builder {
        // Для версии Android не менее 8.0 (Oreo)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = getNotificationChannel()
            val manager = getNotificationManager(context)
            manager.createNotificationChannel(channel)
            NotificationCompat.Builder(context, channel.id)
        } else {
            NotificationCompat.Builder(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNotificationChannel(): NotificationChannel {
        val chanelId = "3939"
        val name = "Channel"
        val description = "Description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(chanelId, name, importance)
        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        return channel
    }


    fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    private fun createNotificationChannel(id: Int, name: CharSequence) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val description = "Channel for ReminderBroadcast $id"


            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id.toString(), name, importance)
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
