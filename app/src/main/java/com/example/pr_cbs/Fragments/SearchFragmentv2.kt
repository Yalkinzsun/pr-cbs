package com.example.pr_cbs.Fragments

import android.content.Intent
import android.os.AsyncTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.Adapters.SearchBooksAdapter
import com.example.pr_cbs.AdvancedSearchActivity
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.RecordStorageFake
import kotlinx.android.synthetic.main.search_fragmentv2.*
import androidx.recyclerview.widget.GridLayoutManager


class SearchFragmentv2(private val getSearchText: () -> String) : Fragment() {
    private lateinit var searchBooksAdapter: SearchBooksAdapter
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mNumberOfBooks: TextView

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
        mNumberOfBooks = view.findViewById(R.id.number_of_books)

        search_filter.setOnClickListener {
            val intent = Intent(context, AdvancedSearchActivity::class.java)
            startActivity(intent)
        }

        searchBooksAdapter = SearchBooksAdapter(this@SearchFragmentv2.context)
        MainBookSearch.layoutManager =
            LinearLayoutManager(this@SearchFragmentv2.context, RecyclerView.VERTICAL, false)

        MainBookSearch.adapter = searchBooksAdapter

        MainBookSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition()

                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    // End of the list is here.

                    if (RecordStorageFake.Instance().canLoadMore())
                        LoadMoreRecordsAsyncTask (mProgressBar) { searchBooksAdapter.notifyDataSetChanged() }.executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR
                        )
                }
            }
        })

        this.loadBooksList()
    }

    private fun loadBooksList() {
        LoadMFNsAsyncTask(
            mProgressBar,
            mNumberOfBooks,
            { this.getSearchText() },
            { searchBooksAdapter.notifyDataSetChanged() },
            {
                Toast.makeText(context, "К сожалению мы ничего не нашли =(", Toast.LENGTH_SHORT)
                    .show()
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }


    class LoadMFNsAsyncTask(
        var mProgressBar: ProgressBar,
        var mNumberOfBooks: TextView,
        private var getSearchText: () -> String,
        private var notifyDataSetChanged: () -> Unit,
        private var onNoResultsFound: () -> Unit
    ) : AsyncTask<Unit, Unit, Unit>() {

        private var hasResult: Boolean = true

        override fun onPreExecute() {
            super.onPreExecute()

            mProgressBar.visibility = VISIBLE

            mNumberOfBooks.text = "0"

            RecordStorageFake.Instance().clear()
            notifyDataSetChanged()
        }

        override fun doInBackground(vararg p0: Unit?) {
            hasResult = RecordStorageFake.Instance().fetchMFNsByQuery(this.getSearchText())
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            RecordStorageFake.Instance().loadNextPage()

            mProgressBar.visibility = GONE

            notifyDataSetChanged()
            mNumberOfBooks.text = RecordStorageFake.Instance().mfNsCount.toString()

            if (!hasResult)
                onNoResultsFound()
        }
    }

    class LoadMoreRecordsAsyncTask(
        var mProgressBar: ProgressBar,
        private var notifyDataSetChanged: () -> Unit
    ) : AsyncTask<Unit, Unit, Unit>() {

        override fun onPreExecute() {
            super.onPreExecute()
            mProgressBar.visibility = VISIBLE
        }

        override fun doInBackground(vararg p0: Unit?) {
            RecordStorageFake.Instance().loadNextPage()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            notifyDataSetChanged()
            mProgressBar.visibility = GONE
        }
    }
}
