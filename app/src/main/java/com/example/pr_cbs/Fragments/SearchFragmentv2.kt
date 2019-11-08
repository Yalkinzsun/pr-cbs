package com.example.pr_cbs.Fragments

import android.content.Intent
import android.os.AsyncTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.Adapters.SearchBooksAdapter
import com.example.pr_cbs.FilterSearchActivity
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.RecordStorage


import kotlinx.android.synthetic.main.search_fragmentv2.*


class SearchFragmentv2(private val getSearchText: () -> String) : Fragment() {

    private lateinit var searchBooksAdapter: SearchBooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.search_fragmentv2, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_filter.setOnClickListener {
            val intent = Intent(context, FilterSearchActivity::class.java)
            startActivity(intent)
        }


        searchBooksAdapter = SearchBooksAdapter(this@SearchFragmentv2.context)
        MainBookSearch.layoutManager =
            LinearLayoutManager(this@SearchFragmentv2.context, RecyclerView.VERTICAL, false)

        MainBookSearch.adapter = searchBooksAdapter

        this.loadBooksList()
    }

    private fun loadBooksList() {
        LoadRecordAsyncTask( { this.getSearchText() }, { searchBooksAdapter.notifyDataSetChanged() } ).execute()
    }


    class LoadRecordAsyncTask( private var getSearchText: () -> String, private var notifyDataSetChanged: () -> Unit): AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg p0: Unit?) {
            RecordStorage.Instance().fetchRecordsByQuery(this.getSearchText())
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            notifyDataSetChanged()
        }
    }
}
