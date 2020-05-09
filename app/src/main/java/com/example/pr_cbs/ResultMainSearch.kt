package com.example.pr_cbs

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


import com.example.pr_cbs.RecordStorage.BookStorage.*
import kotlinx.android.synthetic.main.activity_result_main_search.*


class ResultMainSearch : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_main_search)


        setSupportActionBar(toolbar_main_search_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val intent = intent


        val book = Instance().getRecordById(intent.getIntExtra("Id", 0))


        val f1 = book.lib_1f_num_of_copies
        val f2 = book.lib_2f_num_of_copies
        val f3 = book.lib_3f_num_of_copies
        val f4 = book.lib_4f_num_of_copies
        val f5 = book.lib_5f_num_of_copies
        val f6 = book.lib_6f_num_of_copies
        val d0 = book.lib_do_num_of_copies
        val crb = book.lib_crb_num_of_copies


        val dataFromStorage: Array<String> = arrayOf(f1, f2, f3, f4, f5, f6, d0, crb)


        val textViewList = arrayListOf<TextView>(
            f1_num_of_copies,
            f2_num_of_copies,
            f3_num_of_copies,
            f4_num_of_copies,
            f5_num_of_copies,
            f6_num_of_copies,
            do_num_of_copies,
            crb_num_of_copies
        )

        val blocks =
            arrayListOf<LinearLayout>(ll_f1, ll_f2, ll_f3, ll_f4, ll_f5, ll_f6, ll_do, ll_crb)



        if (book.author != "null null") main_final_book_author.text = book.author
        else main_final_book_author.visibility = GONE

        main_final_book_title.text = book.title

        if (book.publish != "null (null)") main_final_book_publish.text = book.publish
        else {
            main_final_book_publish.visibility = GONE
            main_final_book_publish_text.visibility = GONE
        }

        if (book.series != null) main_final_book_series.text = book.series
        else {
            main_final_book_series.visibility = GONE
            main_final_book_series_text.visibility = GONE
        }


        if (book.year != "null" && book.year != null) main_final_book_year.text = book.year
        else main_final_book_year_block.visibility = GONE


        if (book.size != "null" && book.size != null) main_final_book_size.text = book.size
        else main_final_book_size_block.visibility = GONE

        if (book.lang != "null" && book.lang != null) main_final_book_language.text = book.lang
        else main_final_book_language_block.visibility = GONE

        if (book.ISBN != "null" && book.ISBN != null)  main_final_book_isbn.text = book.ISBN
        else main_final_book_isbn_block.visibility = GONE

        main_final_book_description.text = book.description


//        supportActionBar?.title = "ISBN: " + book.ISBN


        if (book.link != null && book.link != "nullnull") Glide.with(this).load(book.link).into(main_search_result_book_cover)


        if (!setNumberOfCopies(textViewList, dataFromStorage, blocks)) {
            main_search_result_not_found.visibility = VISIBLE
            main_search_result_ll_book_or_deliver.visibility = GONE
        }


    }

    private fun setNumberOfCopies(
        textViewList: ArrayList<TextView>,
        dataFromStorage: Array<String>,
        blocks: ArrayList<LinearLayout>
    ): Boolean {

        var found = false
        for (i in 0 until textViewList.size) {

            try {
                if (dataFromStorage[i] != "0") {
                    val text = "Доступно: ${dataFromStorage[i]} экз."
                    setText(textViewList[i], text)
                    found = true
                } else setVisibility(blocks[i])
            } catch (e: Exception) {
                return false
            }
        }
        return found
    }


    private fun setText(view: TextView, text: String) {
        view.text = text
    }

    private fun setVisibility(view: LinearLayout) {
        view.visibility = GONE
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

