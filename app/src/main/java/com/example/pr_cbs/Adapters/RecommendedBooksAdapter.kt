package com.example.pr_cbs.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.R
import com.example.pr_cbs.Models.RecommendedBook
import com.example.pr_cbs.RecommendedBookResult
import kotlinx.android.synthetic.main.adapter_recommended.view.*



class RecommendedBooksAdapter(private val context: Context?, private val bookList: ArrayList<RecommendedBook>) : RecyclerView.Adapter<RecommendedBooksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.adapter_recommended, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return bookList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val book = bookList[position]
        holder.setData(book, position)

//        holder.name?.text = bookList[p1].name
//        p0.count?.text = bookList[p1].count.toString()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var currentBook: RecommendedBook? = null
        var currentPosition: Int = 0


        init {
            itemView.setOnClickListener {

                val intent = Intent(context, RecommendedBookResult::class.java)
                context!!.startActivity(intent)
            }
        }

        fun setData(book: RecommendedBook?, pos:Int){

            itemView.tvTittleRecommended.text = book!!.name
            itemView.imgV_recommended.setBackgroundResource(book.img_file)

//            itemView.cl_for_book_cover.background = drawable

            this.currentBook = book
            this.currentPosition = pos
        }

//        val name = itemView.findViewById<TextView>(R.id.tvName)
//        val count = itemView.findViewById<TextView>(R.id.tvCount)

    }
}