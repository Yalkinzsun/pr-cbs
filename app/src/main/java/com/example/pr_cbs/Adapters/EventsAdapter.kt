package com.example.pr_cbs.Adapters


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.Models.LatestModel


import com.example.pr_cbs.RecommendedBookResult
import kotlinx.android.synthetic.main.carousel_latest_item.view.*


import android.content.Context
import com.example.pr_cbs.R


class EventsAdapter(private val context: Context?, private val mutableList: ArrayList<LatestModel>): RecyclerView.Adapter<EventsAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carousel_latest_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(mutableList[position])
    }



    inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {


        init {
            itemView!!.setOnClickListener {

                    val intent = Intent(context, RecommendedBookResult::class.java)
                    context!!.startActivity(intent)

            }
        }
        fun bindView(myItem: LatestModel) {
            itemView.my_title.text = myItem.title
            //itemView.imgV_recommended.setBackgroundResource(book.img_file)
           // Glide.with(itemView.context).load(myItem.thumbnail).into(itemView.my_iv)
        }
    }
}