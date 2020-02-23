package com.example.pr_cbs.Fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pr_cbs.MainActivity
import com.example.pr_cbs.R
import android.view.View.*
import android.widget.ProgressBar
import android.widget.TextView
import com.example.pr_cbs.HowToBecomeAReaderActivity
import com.example.pr_cbs.RecordStorage.TakenBookStorage
import kotlinx.android.synthetic.main.to_reader_fragment.*
import com.example.pr_cbs.TakenBooksActivity


class ToReaderFragment : Fragment() {


    private lateinit var mProgressBar: ProgressBar
    private lateinit var mNumberOfBooks: TextView
    private lateinit var mMore: TextView
    private lateinit var libraryCard: String
    private var isDownloadFinished: Boolean = false
    lateinit var sPref: SharedPreferences
    private val APP_PREFERENCES = "pref_settings"
    private val APP_PREFERENCES_LIBRARY_CARD = "library_card"


    override fun onPause() {
        super.onPause()

        if (libraryCard.isNotEmpty()) {
            val editor = sPref.edit()
            editor.putString(APP_PREFERENCES_LIBRARY_CARD, libraryCard)
            editor.apply()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar!!.hide()

        if (sPref.contains(APP_PREFERENCES_LIBRARY_CARD)) {
            // Получаем данные из настроек
            libraryCard = sPref.getString(APP_PREFERENCES_LIBRARY_CARD, "").toString()
            tv_reader_ticket_number.text = libraryCard
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).supportActionBar!!.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.to_reader_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgressBar = view.findViewById(R.id.toReaderProgressBar)
        mNumberOfBooks = view.findViewById(R.id.toReaderNumberOfBooks)
        mMore = view.findViewById(R.id.reader_more_info_about_taken_books)


        sPref = this.activity!!.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)


        if (sPref.contains(APP_PREFERENCES_LIBRARY_CARD) && sPref.getString(
                APP_PREFERENCES_LIBRARY_CARD,
                ""
            ).toString() != "0"
        ) {
            libraryCard = sPref.getString(APP_PREFERENCES_LIBRARY_CARD, "").toString()
            tv_reader_ticket_number.text = libraryCard
            card_view_reader_1.visibility = INVISIBLE
            card_view_reader_2.visibility = VISIBLE

            loadTakenBooks("Microsoft")


        }




        iv_reader_submit2.setOnClickListener {
            if (et_reader_ticket_number.text.toString().isNotEmpty()) {
                libraryCard = et_reader_ticket_number.text.toString()
                card_view_reader_1.visibility = INVISIBLE
                card_view_reader_2.visibility = VISIBLE
                tv_reader_ticket_number.text = libraryCard

                loadTakenBooks("Microsoft")

            }
        }

        iv_reader_clear.setOnClickListener {
            libraryCard = "0"
            val editor = sPref.edit()
            editor.putString(APP_PREFERENCES_LIBRARY_CARD, libraryCard)
            editor.apply()
            card_view_reader_2.visibility = INVISIBLE
            card_view_reader_1.visibility = VISIBLE
        }



        reader_more_info_about_taken_books.setOnClickListener {
           // if (isDownloadFinished) {
                val intent = Intent(context, TakenBooksActivity::class.java)
                startActivity(intent)
           // }
        }


        iv_how_to_become_a_reader.setOnClickListener {
            val intent = Intent(context, HowToBecomeAReaderActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadTakenBooks(ticketNumber: String) {

        context?.let {
            LoadTakenBooksAsyncTask(
                it,
                mMore,
                isDownloadFinished,
                mProgressBar,
                mNumberOfBooks,
                ticketNumber,
                { mNumberOfBooks.text = "-1" }
            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

    }

    class LoadTakenBooksAsyncTask(
        var mContext: Context,
        var mMore: TextView,
        var finishFlag: Boolean,
        var mProgressBar: ProgressBar,
        var mNumberOfTakenBooks: TextView,
        var ticketNumber: String,
        private var onNoResultsFound: () -> Unit

    ) : AsyncTask<Unit, Unit, Unit>() {

        private var hasResult: Boolean = true

        override fun onPreExecute() {
            super.onPreExecute()
            mNumberOfTakenBooks.visibility = INVISIBLE
            mProgressBar.visibility = VISIBLE

            TakenBookStorage.Instance().clear()
        }

        override fun doInBackground(vararg p0: Unit?) {

            this.hasResult = TakenBookStorage.Instance().downloadAllTakenBooks(ticketNumber)

        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            mProgressBar.visibility = GONE

            val sPref: SharedPreferences =
                mContext.getSharedPreferences("pref_settings", MODE_PRIVATE)
            val editor = sPref.edit()

            val takenBookCount = TakenBookStorage.Instance().availableRecordsCount.toString()
            editor.putString("taken_book_count", takenBookCount)
            editor.apply()

            mNumberOfTakenBooks.visibility = VISIBLE

            mNumberOfTakenBooks.text = takenBookCount


            if (!this.hasResult) {
                onNoResultsFound()

            } else {
                finishFlag = true
                mMore.setBackgroundResource(R.drawable.button_order_delivery)
            }
        }
    }


}
