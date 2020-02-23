package com.example.pr_cbs.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.BookRecord
import com.example.pr_cbs.RecordStorage.BookStorage
import com.example.pr_cbs.ResultMainSearch
import kotlinx.android.synthetic.main.adapter_main_search_item.view.*


class SearchBooksAdapter(private val context: Context?) :
    RecyclerView.Adapter<SearchBooksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(context).inflate(R.layout.adapter_main_search_item, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return BookStorage.Instance().availableRecordsCount
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = BookStorage.Instance().getRecordById(position)
        holder.setData(book, position)

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentPosition: Int = 0

        init {
            itemView.setOnClickListener {

                val intent = Intent(context, ResultMainSearch::class.java)
                intent.putExtra("Id", currentPosition)
                context!!.startActivity(intent)
            }
        }

        fun setData(book: BookRecord, pos: Int) {
            itemView.book_author.text = book.author
            itemView.book_tittle.text = book.title
            itemView.book_subjects.text = book.subjects
            itemView.book_publisher.text = book.publish
            itemView.book_series.text = book.series
            itemView.book_year.text = book.year

            if (book.link == "nullnull") {
                itemView.book_cover.setBackgroundResource(R.drawable.book_cover_1)
            } else {
                if (context != null) {

                   Glide.with(context).load(book.link).into(itemView.book_cover)
                }
            }
            this.currentPosition = pos
        }


    }
}