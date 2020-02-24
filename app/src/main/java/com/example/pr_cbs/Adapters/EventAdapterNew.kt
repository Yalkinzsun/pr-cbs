package com.example.pr_cbs.Adapters;


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pr_cbs.EventFullInfo
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.EventRecord
import com.example.pr_cbs.RecordStorage.EventStorage


import com.example.pr_cbs.ResultMainSearch
import kotlinx.android.synthetic.main.adapter_event_item.view.*
import kotlinx.android.synthetic.main.adapter_main_search_item.view.*






class EventAdapterNew(private val context: Context?) :
    RecyclerView.Adapter<EventAdapterNew.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(context).inflate(R.layout.adapter_event_item, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return EventStorage.Instance().availableRecordsCount
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = EventStorage.Instance().getRecordById(position)
        holder.setData(event, position)

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentPosition: Int = 0

        init {

//            itemView.setOnClickListener {
//                val intent = Intent(context, EventFullInfo::class.java)
//                intent.putExtra("event_id", currentPosition)
//                context!!.startActivity(intent)
//            }

            itemView.more_button_event_fragment.setOnClickListener {
                val intent = Intent(context, EventFullInfo::class.java)
                intent.putExtra("event_id", currentPosition)
                context!!.startActivity(intent)
            }
        }

        fun setData(event: EventRecord, pos: Int) {
            if (event.start_date != event.end_date) {

            } else itemView.event_date.text = shortenedDate(event.start_date)

            itemView.event_time.text = event.start_time + " - " + event.end_time
            itemView.event_age_category.text = event.age_category
            itemView.event_name.text = event.name
            itemView.event_additional_information.text = event.additional_information
            itemView.event_library.text = event.library
            itemView.event_address.text = event.address

            if (event.link != "nullnull") {
                if (context != null) {

//                    Picasso.with(context)
//                        .load(event.link)
//                        .into(itemView.event_cover)

               //     Glide.clear(viewHolder.imageView);
                    Glide.with(context)
                        .load(event.link)
                        .into(itemView.event_cover)
                }

            } else {
                itemView.event_cover.setBackgroundResource(R.drawable.cover_event_2)
            }


            this.currentPosition = pos
        }

        private fun shortenedDate(oldDate: String): String {
            var mountDecryption = "января"
            val parts = oldDate.split(".")

            val day = parts[0].toInt()

            when (parts[1].toInt()) {
                2 -> mountDecryption = "февраля"
                3 -> mountDecryption = "марта"
                4 -> mountDecryption = "апреля"
                5 -> mountDecryption = "мая"
                6 -> mountDecryption = "июня"
                7 -> mountDecryption = "июля"
                8 -> mountDecryption = "августа"
                9 -> mountDecryption = "сентября"
                10 -> mountDecryption = "октября"
                11 -> mountDecryption = "ноября"
                12 -> mountDecryption = "декабря"

            }

            return "$day $mountDecryption"
        }


    }
}
