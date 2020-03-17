package com.example.pr_cbs.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.AsyncTask

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
import com.example.pr_cbs.AsyncTasks.LoadMoreBookRecordsAsyncTask
import com.example.pr_cbs.RecordStorage.BookRecord
import kotlin.math.abs


class SearchFragmentv2() : Fragment(), LoadMoreBookRecordsAsyncTask.LoadMoreBookRecordsFinished {

    private lateinit var searchBooksAdapter: SearchBooksAdapter
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mProgressBar2: ProgressBar
    private lateinit var mNumberOfBooks: TextView
    private lateinit var mTextNumberOfBooks: TextView
    private lateinit var mSearchImage: ImageView
    private lateinit var mSearchTextView: TextView
    private var bookCount: String = "0"
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

//               // кол-во элементов на экране
//                val visibleItemCount = layoutManager.childCount
//                // кол-во элементов всего
//
//                val firstVisibleItem =
//                    layoutManager.findFirstVisibleItemPosition()

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

        if (flag1) {

            this.loadBooksList(false)
            (activity as MainActivity).isPressed = false
        }
        if (flag2) {
            this.loadBooksList(false)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

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
                this.loadBooksList(true)
                (activity as MainActivity).setSearchText(mSearchCombination)
                //  mSearchCombination = ""

            }
        }


    }



    private fun loadBooksList(advanced_flag: Boolean) {
        activity?.applicationContext?.let {
            LoadMFNsAsyncTask(
                it,
                bookCount,
                mProgressBar,
                mNumberOfBooks,
                mTextNumberOfBooks,
                advanced_flag,
                mSearchImage,
                mSearchTextView,
                mSearchCombination,
                { (activity as MainActivity).getSearchText() },
                { searchBooksAdapter.notifyDataSetChanged() },
                {
                    mSearchImage.visibility = VISIBLE
                    mSearchTextView.visibility = VISIBLE
                    val errorText =
                        "К сожалению, по запросу\n\"${(activity as MainActivity).getSearchText()}\"\nничего не найдено"
                    mSearchTextView.text = errorText
                    (activity as MainActivity).setSearchText("")
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

        (activity as MainActivity).keyboardSearchIconPressed = false
    }


    class LoadMFNsAsyncTask(
        var context: Context,
        var bookCount: String,
        var mProgressBar: ProgressBar,
        var mNumberOfBooks: TextView,
        var mTextNumberOfBooks: TextView,
        var advanced_flag: Boolean,
        var mSearchImage: ImageView,
        var mSearchTextView: TextView,

        var mSearchCombination: String,
        private var getSearchText: () -> String,
        private var notifyDataSetChanged: () -> Unit,
        private var onNoResultsFound: () -> Unit
    ) : AsyncTask<Unit, Unit, Unit>() {

        private var hasResult: Boolean = true

        override fun onPreExecute() {
            super.onPreExecute()


            mSearchImage.visibility = INVISIBLE
            mSearchTextView.visibility = INVISIBLE
            mProgressBar.visibility = VISIBLE

            BookStorage.Instance().clear()
            notifyDataSetChanged()
        }

        override fun doInBackground(vararg p0: Unit?) {
            if (mSearchCombination != "") {

                if (advanced_flag) this.hasResult =
                    BookStorage.Instance().fetchMFNsByQueryAdvanced(mSearchCombination)
                else this.hasResult =
                    BookStorage.Instance().fetchMFNsByQuery(mSearchCombination)

            } else this.hasResult = BookStorage.Instance().fetchMFNsByQuery(this.getSearchText())


            BookStorage.Instance().loadNextPage(0)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)


            mProgressBar.visibility = GONE
            notifyDataSetChanged()


            val sPref: SharedPreferences =
                context.getSharedPreferences("pref_settings", Context.MODE_PRIVATE)
            val editor = sPref.edit()
            bookCount = BookStorage.Instance().mfNsCount.toString()
            editor.putString("book_count", bookCount)
            editor.apply()

            mTextNumberOfBooks.visibility = VISIBLE
            mNumberOfBooks.visibility = VISIBLE

            mNumberOfBooks.text = bookCount


            if (!this.hasResult) {
                onNoResultsFound()
            } else {
                mSearchImage.visibility = INVISIBLE
                mSearchTextView.visibility = INVISIBLE
            }
        }
    }

}
