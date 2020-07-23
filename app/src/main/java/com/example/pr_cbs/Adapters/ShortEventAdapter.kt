package com.example.pr_cbs.Adapters;


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.EventRecord
import com.example.pr_cbs.RecordStorage.EventStorage
import kotlinx.android.synthetic.main.adapter_short_event_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class ShortEventAdapter(private val context: Context?) :
    RecyclerView.Adapter<ShortEventAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(context).inflate(R.layout.adapter_short_event_item, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return 2
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = EventStorage.Instance().getRecordById(position)
        holder.setData(event, position)

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentPosition: Int = 0


        fun setData(event: EventRecord, pos: Int) {

            val startDate: String = event.start_date

            var format = SimpleDateFormat("yyyy-MM-dd", Locale("ru"))
            val newDate = format.parse(startDate)
            format = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
            val newStartDate = format.format(newDate)



            itemView.home_day_number.text = getDay(newStartDate).toString()
            itemView.home_weekday.text = getMonth(newStartDate)
            itemView.home_event_name.text = event.name
            itemView.home_event_location.text = event.library


            this.currentPosition = pos
        }

        private fun getDay(line: String): Int {
            val parts = line.split(".")
            return parts[0].toInt()
        }

        private fun getMonth(line: String): String {
            var mountDecryption = "янв"
            val parts = line.split(".")

            when (parts[1].toInt()) {
                2 -> mountDecryption = "фев"
                3 -> mountDecryption = "мар"
                4 -> mountDecryption = "апр"
                5 -> mountDecryption = "мая"
                6 -> mountDecryption = "июн"
                7 -> mountDecryption = "июл"
                8 -> mountDecryption = "авг"
                9 -> mountDecryption = "сен"
                10 -> mountDecryption = "окт"
                11 -> mountDecryption = "ноя"
                12 -> mountDecryption = "дек"
            }
            return mountDecryption
        }


    }
}
