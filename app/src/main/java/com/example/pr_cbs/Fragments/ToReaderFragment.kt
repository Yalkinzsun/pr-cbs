package com.example.pr_cbs.Fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.view.View.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.pr_cbs.*
import com.example.pr_cbs.AsyncTasks.LoadTakenBooksAsyncTask
import com.example.pr_cbs.RecordStorage.TakenBookStorage
import kotlinx.android.synthetic.main.to_reader_fragment.*


class ToReaderFragment : Fragment(), LoadTakenBooksAsyncTask.LoadTakenBooksFinished {

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar!!.hide()

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

        var libraryCard: String

        if (MainActivity.checkSharedPreferenceAvailability(
                "library_card",
                this.activity!!
            ) && MainActivity.getFromSharedPreferences("library_card", this.activity!!) != "0"
        ) {

            libraryCard = MainActivity.getFromSharedPreferences("library_card", this.activity!!)
            tv_reader_ticket_number.text = libraryCard
            card_view_reader_1.visibility = INVISIBLE
            card_view_reader_2.visibility = VISIBLE
            loadTakenBooks(libraryCard)

        }


        iv_reader_submit2.setOnClickListener {
            if (et_reader_ticket_number.text.toString().isNotEmpty()) {

                if (et_reader_ticket_number.text.toString().length == 8) {

                    libraryCard = et_reader_ticket_number.text.toString()
                    libraryCard = libraryCard.substring(0, 2) + " " + libraryCard.substring(2, 8)
                    MainActivity.putInSharedPreferences(
                        "library_card",
                        libraryCard,
                        this.activity!!
                    )
                    card_view_reader_1.visibility = INVISIBLE
                    card_view_reader_2.visibility = VISIBLE
                    tv_reader_ticket_number.text = libraryCard

                    loadTakenBooks(libraryCard)
                } else Toast.makeText(
                    this@ToReaderFragment.context,
                    "Номер билета состоит из 8 цифр!",
                    Toast.LENGTH_LONG
                ).show()

            } else Toast.makeText(
                this@ToReaderFragment.context,
                "Введите номер читательского билета!",
                Toast.LENGTH_LONG
            ).show()
        }

        iv_reader_clear.setOnClickListener {
            MainActivity.putInSharedPreferences("library_card", "0", this.activity!!)
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

        iv_extend_book.setOnClickListener {
//            val intent = Intent(context, WebActivity::class.java)
//            intent.putExtra("link", "https://forms.yandex.ru/u/5d7b923c4398960967b56616/?iframe=1")
//            startActivity(intent)
            val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.yandex.ru/u/5d7b923c4398960967b56616/?iframe=1"))
            startActivity(browseIntent)


        }

        iv_ask_librarian.setOnClickListener {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra("link", "https://forms.yandex.ru/u/5d7f7ed1f198c02605ba8364/?iframe=1")
            startActivity(intent)

        }

    }


    override fun allTakenBooksLoaded(resCode: Int) {

        val takenBookCount = TakenBookStorage.Instance().availableRecordsCount.toString()
        MainActivity.putInSharedPreferences("taken_book_count", takenBookCount, this.activity!!)

        toReaderProgressBar.visibility = INVISIBLE

        when (resCode) {

            0 -> {
                toReaderNumberOfBooks.text = takenBookCount
                reader_more_info_about_taken_books.setBackgroundResource(R.drawable.button_order_delivery)
                reader_more_info_about_taken_books.isEnabled = true

            }
            1 -> toReaderNumberOfBooks.text = "0"
            2 -> Toast.makeText(
                this@ToReaderFragment.context,
                "Ошибка получения данных от сервера",
                Toast.LENGTH_LONG
            ).show()
            3 -> Toast.makeText(
                this@ToReaderFragment.context,
                "Вы не подключены к Интернету",
                Toast.LENGTH_LONG
            ).show()

        }
    }


    private fun loadTakenBooks(ticketNumber: String) {
        val internetConnection = (activity as MainActivity).isNetworkConnected()
        toReaderProgressBar.visibility = VISIBLE
        LoadTakenBooksAsyncTask(
            this@ToReaderFragment,
            ticketNumber,
            internetConnection
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }
}
