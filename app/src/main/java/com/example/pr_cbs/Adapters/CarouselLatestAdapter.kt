package com.example.pr_cbs.Adapters


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


import kotlinx.android.synthetic.main.carousel_latest_item.view.*


import android.content.Context
import com.bumptech.glide.Glide
import com.example.pr_cbs.LatestBookInfo
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.BookRecord
import com.example.pr_cbs.RecordStorage.LatestBookStorage



class CarouselLatestAdapter(private val context: Context?) : RecyclerView.Adapter<CarouselLatestAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carousel_latest_item, parent, false)
        return MyViewHolder(view)
    }



    override fun getItemCount(): Int {
        return LatestBookStorage.Instance().availableRecordsCount
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
     //   holder.bindView(mutableList[position])
        val latestBook = LatestBookStorage.Instance().getRecordById(position)
        holder.setData(latestBook, position)
    }


    inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private var currentPosition: Int = 0

        init {
            itemView!!.setOnClickListener {

                val intent = Intent(context, LatestBookInfo::class.java)
                intent.putExtra("Id", currentPosition)
                context!!.startActivity(intent)

            }
        }

        fun setData(book: BookRecord, pos: Int) {

            itemView.latest_book_title.text = book.title

            if (book.link == "nullnull") {

                when ((0..2).random()){
                    0 ->  itemView.latest_book_cover.setBackgroundResource(R.drawable.book_cover_1)
                    1 ->  itemView.latest_book_cover.setBackgroundResource(R.drawable.book_cover_2)
                    2 ->  itemView.latest_book_cover.setBackgroundResource(R.drawable.book_cover_3)
                }
            } else {
                if (context != null) Glide.with(context).load(book.link).into(itemView.latest_book_cover)
            }
            this.currentPosition = pos


        }
    }
}