package com.example.pr_cbs.Fragments

import android.content.Context
import android.content.Intent
import android.os.AsyncTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.Adapters.SearchBooksAdapter
import com.example.pr_cbs.AdvancedSearchActivity
import com.example.pr_cbs.MainActivity
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.BookStorage
import kotlinx.android.synthetic.main.search_fragmentv2.*
import android.widget.*
import com.example.pr_cbs.AsyncTasks.LoadAllBookMFNsAsyncTask
import com.example.pr_cbs.AsyncTasks.LoadMoreBookRecordsAsyncTask
import com.example.pr_cbs.RecordStorage.BookRecord


class SearchFragment : Fragment(), LoadMoreBookRecordsAsyncTask.LoadMoreBookRecordsFinished,
    LoadAllBookMFNsAsyncTask.LoadAllBookMFNsFinished {

    private lateinit var searchBooksAdapter: SearchBooksAdapter
    private lateinit var mNumberOfBooks: TextView
    private lateinit var mTextNumberOfBooks: TextView
    private lateinit var mSearchImage: ImageView
    private lateinit var mSearchTextView: TextView
    private var mSearchCombination: String = ""
    private var isDataLoading = false


    override fun onDestroy() {
        super.onDestroy()
        zeroing((SearchFragment@ this.context)!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragmentv2, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mNumberOfBooks = view.findViewById(R.id.number_of_books)
        mTextNumberOfBooks = view.findViewById(R.id.txt_found)
        mSearchImage = view.findViewById(R.id.search_fragment_image)
        mSearchTextView = view.findViewById(R.id.search_fragment_text)


        if (MainActivity.checkSharedPreferenceAvailability(
                "book_count",
                this.activity!!
            ) && MainActivity.getFromSharedPreferences(
                "book_count",
                this.activity!!
            ) != "0"
        ) {

            val text = " " + MainActivity.getFromSharedPreferences("book_count", this.activity!!)
            mNumberOfBooks.text = text
            mTextNumberOfBooks.visibility = VISIBLE
            mNumberOfBooks.visibility = VISIBLE
        }


        search_filter.setOnClickListener {

            MainActivity.putInSharedPreferences(
                "search_text", (activity as MainActivity).getSearchText(),
                this@SearchFragment.context!!
            )


            val intent = Intent(context, AdvancedSearchActivity::class.java)
            intent.putExtra("search_text", (activity as MainActivity).getSearchText())
            startActivityForResult(intent, 1)
        }

        searchBooksAdapter = SearchBooksAdapter(this@SearchFragment.context)


        MainBookSearch.layoutManager =
            LinearLayoutManager(this@SearchFragment.context, RecyclerView.VERTICAL, false)

        MainBookSearch.adapter = searchBooksAdapter

        MainBookSearch.setHasFixedSize(false)

        MainBookSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val currentVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount


                if (!isDataLoading) {
                    if (currentVisibleItem == totalItemCount - 1) {
                        if (BookStorage.Instance().canLoadMore()) {
                            isDataLoading = true

                            LoadMoreBookRecordsAsyncTask(
                                this@SearchFragment,
                                currentVisibleItem
                            ) { this@SearchFragment.showSearchProgressBar() }.executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR
                            )
                        }
                    }
                }
            }
        })


        // проверка адаптера на наличие элеметов
        if (searchBooksAdapter.itemCount != 0) {
            mSearchImage.visibility = INVISIBLE
            mSearchTextView.visibility = INVISIBLE
        } else {
            mTextNumberOfBooks.visibility = INVISIBLE
            mNumberOfBooks.visibility = INVISIBLE
        }

        val flag1: Boolean = (activity as MainActivity).isSearchIconPressed()
        val flag2: Boolean = (activity as MainActivity).isKeyboardSearchIconPressed()
        val internetConnection = (activity as MainActivity).isNetworkConnected()

        // Загрузка данных
        if (flag1) {

            this.loadBooksList(false, internetConnection, true)
            (activity as MainActivity).isPressed = false
        }
        if (flag2) {
            this.loadBooksList(false, internetConnection, true)
            (activity as MainActivity).keyboardSearchIconPressed = false
        }

        if (MainActivity.getFromSharedPreferences(
                "circle_notification",
                SearchFragment@ this.context!!
            ) == "true"
        ) search_fragment_notification.visibility = VISIBLE


    }


    override fun moreBooksLoaded(position: Int) {
        hideSearchProgressBar()
        val moreBooks: ArrayList<BookRecord> = BookStorage.Instance().localRecords
        val size = BookStorage.Instance().localRecords.size

//        Toast.makeText(this@SearchFragment.context, moreBooks.size.toString(), Toast.LENGTH_SHORT)
//            .show()

        for (i in position + 1..size) {
            searchBooksAdapter.notifyItemChanged(i)
        }
        isDataLoading = false

    }

    override fun allBookMFNsLoaded(resCode: Int, bookCount: String) {
        val text = " $bookCount"
        mNumberOfBooks.text = text
        mTextNumberOfBooks.visibility = VISIBLE
        mNumberOfBooks.visibility = VISIBLE

        MainActivity.putInSharedPreferences("book_count", bookCount, this@SearchFragment.context!!)

        hideSearchProgressBar()

        when (resCode) {

            //не удалось найти книги по запросу - 1
            1 -> bookNotFound()
            // ошибка получения данных от сервера
            2 -> serverError()
            // нет подключения к интернету
            3 -> noInternetConnection()

        }
    }


    private fun showSearchProgressBar() {
        search_progressBar_block.visibility = VISIBLE
    }

    private fun hideSearchProgressBar() {
        search_progressBar_block.visibility = INVISIBLE
    }

    private fun preparingForSearch() {
        search_fragment_server_error_block.visibility = INVISIBLE
        search_fragment_not_found_block.visibility = INVISIBLE
        search_fragment_no_internet_block.visibility = INVISIBLE
        search_fragment_start_block.visibility = INVISIBLE
        showSearchProgressBar()
    }

    private fun serverError() {
        search_fragment_server_error_block.visibility = VISIBLE
    }

    private fun noInternetConnection() {
        search_fragment_no_internet_block.visibility = VISIBLE
    }

    private fun bookNotFound() {
        search_fragment_not_found_block.visibility = VISIBLE
        val errorText =
            "К сожалению, по запросу\n\"${(activity as MainActivity).getSearchText()}\"\nничего не найдено"
        search_fragment_not_found_text.text = errorText
        (activity as MainActivity).setSearchText("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val internetConnection = (activity as MainActivity).isNetworkConnected()
        if (data == null) return
        var searchString = ""
        when (requestCode) {
            1 -> {

                val titleAdvancedSearch = data.getStringExtra("title")
                if (titleAdvancedSearch != "null") searchString += "\"T=$titleAdvancedSearch$\""

                val authorAdvancedSearch = data.getStringExtra("author")
                if (authorAdvancedSearch != "null") {
                    searchString += if (searchString == "") "\"A=$authorAdvancedSearch$\""
                    else "*\"A=$authorAdvancedSearch$\""
                }

                val subjectsAdvancedSearch = data.getStringExtra("subjects")
                if (subjectsAdvancedSearch != "null") {

                    if (searchString == "") searchString += "\"S=$subjectsAdvancedSearch$\""
                    else searchString += "*\"TEMS=$subjectsAdvancedSearch\""
                }

                val seriesAdvancedSearch = data.getStringExtra("series")
                if (seriesAdvancedSearch != "null") {
                    if (searchString == "") searchString += "\"TS=$seriesAdvancedSearch\""
                    else searchString += "*\"TS=$seriesAdvancedSearch\""
                }

                val personAdvancedSearch = data.getStringExtra("person")
                if (personAdvancedSearch != "null") {

                    if (searchString == "") searchString += "\"PI=$personAdvancedSearch$\""
                    else searchString += "*\"PI=$personAdvancedSearch$\""
                }

                val availabilityAdvancedSearch = data.getStringExtra("checkBox_available")
                if (availabilityAdvancedSearch != "false") {
                    if (searchString == "") searchString += "\"INVST=0\""
                    else searchString += "*(\"INVST=1\")"
                }

                val centerPushkin = data.getStringExtra("checkBox_centerPushkin")
                val center = data.getStringExtra("checkBox_center")
                val kirov = data.getStringExtra("checkBox_kirov")
                val lavreneva = data.getStringExtra("checkBox_lavreneva")
                val gaidar = data.getStringExtra("checkBox_gaidar")
                val lib2 = data.getStringExtra("checkBox_2lib")
                val lib3 = data.getStringExtra("checkBox_3lib")

                if (centerPushkin != "false" || center != "false" || kirov != "false" || lavreneva != "false" || gaidar != "false" || lib2 != "false" || lib3 != "false") {
                    var libs = "("
                    if (centerPushkin != "false") libs += "\"MHR=црб\""
                    if (center != "false") libs += "\"MHR=do\"+"
                    if (kirov != "false") libs += "\"MHR=5ф\"+"
                    if (lavreneva != "false") libs += "\"MHR=2ф\"+"
                    if (gaidar != "false") libs += "\"MHR=3ф\"+"
                    if (lib2 != "false") libs += "\"MHR=6ф\"+"
                    if (lib3 != "false") libs += "\"MHR=4ф\"+"

                    libs = libs.substring(0, libs.length - 1)

                    if (searchString == "") searchString += libs + ")"
                    else searchString += "*" + libs + ")"
                }

                val years = data.getStringExtra("date")

                if (years != "null" && years != "()") {

                    if (searchString == "") searchString += years
                    else searchString += "*$years"
                }

                val addition = data.getBooleanExtra("addition", false)

                if (searchString != "") {
                    search_fragment_notification.visibility = VISIBLE
                    MainActivity.putInSharedPreferences(
                        "circle_notification",
                        "true",
                        SearchFragment@ this.context!!
                    )
                    val textFromSearchLine = (activity as MainActivity).getSearchText()

                    if (addition && textFromSearchLine != "") {
                        mSearchCombination = "\"K=$textFromSearchLine\"*($searchString)"
                        this.loadBooksList(true, internetConnection, false)

                    } else {
                        mSearchCombination = searchString
                        this.loadBooksList(true, internetConnection, false)
                        //(activity as MainActivity).setSearchText("")
                    }



//                    Toast.makeText(
//                        SearchFragment@ this.context,
//                        mSearchCombination,
//                        Toast.LENGTH_LONG
//                    ).show()
                    //   (activity as MainActivity).setSearchText(mSearchCombination)

                }
            }
        }
    }


    private fun loadBooksList(
        advanced_flag: Boolean,
        internetConnection: Boolean,
        zeroing: Boolean
    ) {
        if (!advanced_flag) search_fragment_notification.visibility = INVISIBLE
        //  else zeroing((SearchFragment@this.context)!!)

        if (zeroing) zeroing((SearchFragment@ this.context)!!)

        activity?.applicationContext?.let {
            LoadAllBookMFNsAsyncTask(
                this@SearchFragment,
                advanced_flag,
                mSearchCombination,
                { (activity as MainActivity).getSearchText() },
                { searchBooksAdapter.notifyDataSetChanged() },
                { this@SearchFragment.preparingForSearch() },
                internetConnection
            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

        (activity as MainActivity).keyboardSearchIconPressed = false
    }


    private fun zeroing(context: Context) {
        MainActivity.putInSharedPreferences("circle_notification", "", context)
        MainActivity.putInSharedPreferences("adv_title", "", context)
        MainActivity.putInSharedPreferences("adv_author", "", context)
        MainActivity.putInSharedPreferences("adv_subjects", "", context)
        MainActivity.putInSharedPreferences("adv_series", "", context)
        MainActivity.putInSharedPreferences("adv_person", "", context)
        MainActivity.putInSharedPreferences("adv_cb_available", "", context)
        MainActivity.putInSharedPreferences("adv_cb_center", "", context)
        MainActivity.putInSharedPreferences("adv_cb_kirov", "", context)
        MainActivity.putInSharedPreferences("adv_cb_lavreneva", "", context)
        MainActivity.putInSharedPreferences("adv_cb_gaidar", "", context)
        MainActivity.putInSharedPreferences("adv_cb_2lib", "", context)
        MainActivity.putInSharedPreferences("adv_cb_3lib", "", context)
        MainActivity.putInSharedPreferences("adv_start_year", "", context)
        MainActivity.putInSharedPreferences("adv_end_year", "", context)
    }
}
