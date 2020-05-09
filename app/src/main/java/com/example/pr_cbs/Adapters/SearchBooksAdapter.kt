package com.example.pr_cbs.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
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

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

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

            if (book.author != "null null") itemView.book_author.text = book.author
            else itemView.book_author.visibility = GONE

            itemView.book_tittle.text = book.title


            if (book.subjects != null) itemView.book_subjects.text = book.subjects
            else itemView.main_search_subject_block.visibility = GONE


            if (book.publish != "null (null)") itemView.book_publisher.text = book.publish
            else itemView.main_search_publisher_block.visibility = GONE

            if (book.series != null) itemView.book_series.text = book.series
            else itemView.main_search_series_block.visibility = GONE


            if (book.year != "null" && book.year != null) itemView.book_year.text = book.year
            else itemView.main_search_year_block.visibility = GONE


            if (book.link == "nullnull") {

                if (itemView.book_cover.drawable == null) {

                    itemView.book_cover.setBackgroundResource(R.drawable.book_cover_3)

//                    when ((0..2).random()) {
//                        0 -> itemView.book_cover.setBackgroundResource(R.drawable.book_cover_1)
//                        1 -> itemView.book_cover.setBackgroundResource(R.drawable.book_cover_2)
//                        2 -> itemView.book_cover.setBackgroundResource(R.drawable.book_cover_3)
//                    }
                }

            } else {
                if (context != null) {

                    Glide.with(context).load(book.link).into(itemView.book_cover)
                }
            }

            if (book.num_of_all_available_copies != "0") {
                val text = "доступно ${book.num_of_all_available_copies} экз."
                itemView.main_search_available_copies.text = text
                itemView.main_search_available_copies.setBackgroundResource(R.drawable.copies_info_green)
            } else {
                itemView.main_search_available_copies.text = "нет в наличии"
                itemView.main_search_available_copies.setBackgroundResource(R.drawable.copies_info_grey)
            }

            this.currentPosition = pos
        }


    }
}