package com.example.pr_cbs.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pr_cbs.R
import com.example.pr_cbs.RecommendedBookInfo
import com.example.pr_cbs.RecordStorage.BookRecord
import com.example.pr_cbs.RecordStorage.RecommendedBookStorage
import com.example.pr_cbs.ResultMainSearch
import kotlinx.android.synthetic.main.adapter_recommended.view.*


class RecommendedBooksAdapter(private val context: Context?) :
    RecyclerView.Adapter<RecommendedBooksAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.adapter_recommended, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return RecommendedBookStorage.Instance().availableRecordsCount
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val latestBook = RecommendedBookStorage.Instance().getRecordById(position)
        holder.setData(latestBook, position)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentPosition: Int = 0


        init {
            itemView.setOnClickListener {

                val intent = Intent(context, RecommendedBookInfo::class.java)
                intent.putExtra("Id", currentPosition)
                context!!.startActivity(intent)
            }
        }


        fun setData(book: BookRecord, pos: Int) {

            itemView.tv_recommended_title.text = book.title

            if (book.link == "nullnull") {

                when ((0..2).random()){
                    0 ->  itemView.iv_recommended_cover.setBackgroundResource(R.drawable.book_cover_1)
                    1 ->  itemView.iv_recommended_cover.setBackgroundResource(R.drawable.book_cover_2)
                    2 ->  itemView.iv_recommended_cover.setBackgroundResource(R.drawable.book_cover_3)
                }
            } else {
                if (context != null) {

                    Glide.with(context).load(book.link).into(itemView.iv_recommended_cover)
                }
            }
            this.currentPosition = pos
        }
    }
}