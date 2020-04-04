package com.example.pr_cbs.Fragments

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

            mNumberOfBooks.text = MainActivity.getFromSharedPreferences("book_count",  this.activity!!)
            mTextNumberOfBooks.visibility = VISIBLE
            mNumberOfBooks.visibility = VISIBLE
        }


        search_filter.setOnClickListener {
            val intent = Intent(context, AdvancedSearchActivity::class.java)
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

            this.loadBooksList(false, internetConnection)
            (activity as MainActivity).isPressed = false
        }
        if (flag2) {
            this.loadBooksList(false, internetConnection)
            (activity as MainActivity).keyboardSearchIconPressed = false
        }
    }


    override fun moreBooksLoaded(position: Int) {
        hideSearchProgressBar()
        val moreBooks: ArrayList<BookRecord> = BookStorage.Instance().localRecords
        val size = BookStorage.Instance().localRecords.size

        Toast.makeText(this@SearchFragment.context, moreBooks.size.toString(), Toast.LENGTH_SHORT)
            .show()

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
        search_fragment_start_block.visibility = GONE
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


}
