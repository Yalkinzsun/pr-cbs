package com.example.pr_cbs.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.TakenBookRecord
import com.example.pr_cbs.RecordStorage.TakenBookStorage
import kotlinx.android.synthetic.main.adapter_main_search_item.view.*
import kotlinx.android.synthetic.main.adapter_taken_book_item.view.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class TakenBooksAdapter(private val context: Context?) :
    RecyclerView.Adapter<TakenBooksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(context).inflate(R.layout.adapter_taken_book_item, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return TakenBookStorage.Instance().availableRecordsCount
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = TakenBookStorage.Instance().getRecordById(position)
        holder.setData(book, position)

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentPosition: Int = 0

        fun setData(book: TakenBookRecord, pos: Int) {
            itemView.taken_book_date_book_taken.text = book.date_book_taken
            itemView.taken_book_description.text = book.description


            val currentDate =
                SimpleDateFormat("yyyy.MM.dd", Locale("ru")).format(Calendar.getInstance().time)
            val currentDateLatest: Date? =
                SimpleDateFormat("yyyy.MM.dd", Locale("ru")).parse(currentDate)

            val savedDateLatest: Date? =
                SimpleDateFormat("yyyy.MM.dd", Locale("ru")).parse(book.date_book_taken)

            val diff = currentDateLatest!!.time - savedDateLatest!!.time
            val hours = TimeUnit.MILLISECONDS.toDays(diff)

            itemView.taken_book_number_of_days.text = hours.toString()


            this.currentPosition = pos
        }


    }
}