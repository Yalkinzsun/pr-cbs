package com.example.pr_cbs.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build

import android.os.Bundle
import android.util.Log
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
import kotlin.math.abs


class SearchFragmentv2() : Fragment(), LoadMoreBookRecordsAsyncTask.LoadMoreBookRecordsFinished,
    LoadAllBookMFNsAsyncTask.LoadAllBookMFNsFinished {

    private lateinit var searchBooksAdapter: SearchBooksAdapter
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mProgressBar2: ProgressBar
    private lateinit var mNumberOfBooks: TextView
    private lateinit var mTextNumberOfBooks: TextView
    private lateinit var mSearchImage: ImageView
    private lateinit var mSearchTextView: TextView
    private var mSearchCombination: String = ""
    private var isDataLoading = false


    lateinit var sPref: SharedPreferences
    // имя файла настроек
    private val APP_PREFERENCES = "pref_settings"
    private val APP_PREFERENCES_BOOK_COUNT = "book_count"


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

        mProgressBar = view.findViewById(R.id.progressBar)
        mProgressBar2 = view.findViewById(R.id.progressBar2)
        mNumberOfBooks = view.findViewById(R.id.number_of_books)
        mTextNumberOfBooks = view.findViewById(R.id.txt_found)
        mSearchImage = view.findViewById(R.id.search_fragment_image)
        mSearchTextView = view.findViewById(R.id.search_fragment_text)


        sPref = this.activity!!.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        if (sPref.contains(APP_PREFERENCES_BOOK_COUNT) && sPref.getString(
                APP_PREFERENCES_BOOK_COUNT,
                ""
            ).toString() != "0"
        ) {
            mNumberOfBooks.text = sPref.getString(APP_PREFERENCES_BOOK_COUNT, "").toString()
            mTextNumberOfBooks.visibility = VISIBLE
            mNumberOfBooks.visibility = VISIBLE
        }


        search_filter.setOnClickListener {
            val intent = Intent(context, AdvancedSearchActivity::class.java)
            startActivityForResult(intent, 1)
        }

        searchBooksAdapter = SearchBooksAdapter(this@SearchFragmentv2.context)



        MainBookSearch.layoutManager =
            LinearLayoutManager(this@SearchFragmentv2.context, RecyclerView.VERTICAL, false)

        MainBookSearch.adapter = searchBooksAdapter

        MainBookSearch.setHasFixedSize(false)

        MainBookSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager


                val currentVisibleItem =
                    layoutManager.findLastCompletelyVisibleItemPosition()//какая позиция первого элемента
                val totalItemCount = layoutManager.itemCount


                if (!isDataLoading) {
                    if (currentVisibleItem == totalItemCount - 1) {
                        if (BookStorage.Instance().canLoadMore()) {
                            isDataLoading = true
                            // Toast.makeText(this@SearchFragmentv2.context, currentVisibleItem.toString(), Toast.LENGTH_SHORT).show()

                            LoadMoreBookRecordsAsyncTask(
                                this@SearchFragmentv2,
                                currentVisibleItem,
                                mProgressBar2
                            ).executeOnExecutor(
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

            this.loadBooksList(false, internetConnection)
            (activity as MainActivity).isPressed = false
        }
        if (flag2) {
            this.loadBooksList(false, internetConnection)
            (activity as MainActivity).keyboardSearchIconPressed = false
        }
    }


    override fun moreBooksLoaded(position: Int) {
        val moreBooks: ArrayList<BookRecord> = BookStorage.Instance().localRecords
        val size = BookStorage.Instance().localRecords.size

        Toast.makeText(this@SearchFragmentv2.context, moreBooks.size.toString(), Toast.LENGTH_SHORT)
            .show()

        for (i in position + 1..size) {
            searchBooksAdapter.notifyItemChanged(i)
        }
        isDataLoading = false

    }

    override fun allBookMFNsLoaded(resCode: Int, bookCount: String) {

        mNumberOfBooks.text = " " + bookCount
        mTextNumberOfBooks.visibility = VISIBLE
        mNumberOfBooks.visibility = VISIBLE

        val sPref: SharedPreferences = this@SearchFragmentv2.context!!.getSharedPreferences(
            "pref_settings",
            Context.MODE_PRIVATE
        )
        val editor = sPref.edit()
        editor.putString("book_count", bookCount)
        editor.apply()

        hideProgressBar()

        when (resCode) {
            //ошибок не произошло - 0
//            0 -> {}
            //не удалось найти книги по запросу - 1
            1 -> bookNotFound()

            // ошибка получения данных от сервера
            2 -> serverError()
            // нет подключения к интернету
            3 -> noInternetConnection()


        }

    }

    private fun preparingForSearch() {
        search_fragment_start_block.visibility = GONE
        progressBar2.visibility = VISIBLE

    }

    fun hideProgressBar() {
        progressBar2.visibility = INVISIBLE
    }


    fun serverError() {
        search_fragment_server_error_block.visibility = VISIBLE

    }

    fun noInternetConnection() {
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
        //TODO
        val internetConnection = (activity as MainActivity).isNetworkConnected()
        if (data == null) return
        when (requestCode) {
            //  1 ->  mSearchCombination = data.getStringExtra("name")
            1 -> {
//          "\"G=2011\" * \"A=Чуков$\""

                val titleStart = data.getStringExtra("title")
                val title = "\"T=$titleStart${if (titleStart != "") "$" else ""}\""

                val author = data.getStringExtra("author")

                mSearchCombination = "$title+\"A=$author$\""
                //                     ("\"T=Google$\" * \"A=Машнин$\"");
                this.loadBooksList(true, internetConnection)
                (activity as MainActivity).setSearchText(mSearchCombination)
                //  mSearchCombination = ""

            }
        }


    }


    private fun loadBooksList(advanced_flag: Boolean, internetConnection: Boolean) {
        activity?.applicationContext?.let {
            LoadAllBookMFNsAsyncTask(
                this@SearchFragmentv2,
                advanced_flag,
                mSearchCombination,
                { (activity as MainActivity).getSearchText() },
                { searchBooksAdapter.notifyDataSetChanged() },
                { this@SearchFragmentv2.preparingForSearch() },
                internetConnection
            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

        (activity as MainActivity).keyboardSearchIconPressed = false
    }


}
