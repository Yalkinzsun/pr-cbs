package com.example.pr_cbs.Adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.BookSearchResult2
import com.example.pr_cbs.R
import com.example.pr_cbs.Models.Book
import kotlinx.android.synthetic.main.adapter_item_layout.view.*


class BookAdapter(private val context: Context, private val bookList: List<Book>) : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.adapter_item_layout, parent, false)
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

        var currentBook: Book? = null
        var currentPosition: Int = 0

        init {
            itemView.setOnClickListener {

                val intent = Intent(context, BookSearchResult2::class.java)
                context.startActivity(intent)
            }
        }

        fun setData(book: Book?, pos:Int){
            itemView.tvName.text = book!!.name
            itemView.tvCount.text = book.count.toString()

            this.currentBook = book
            this.currentPosition = pos
        }

//        val name = itemView.findViewById<TextView>(R.id.tvName)
//        val count = itemView.findViewById<TextView>(R.id.tvCount)

    }
}