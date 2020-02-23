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
import com.example.pr_cbs.ResultMainSearch
import kotlinx.android.synthetic.main.adapter_main_search_item.view.*


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
            itemView.book_author.text = book.author
            itemView.book_tittle.text = book.title
            itemView.book_publisher.text = book.publish
            itemView.book_year.text = book.year

            this.currentPosition = pos
        }


    }
}