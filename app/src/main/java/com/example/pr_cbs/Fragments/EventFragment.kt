package com.example.pr_cbs.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*


import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.Adapters.EventAdapterNew
import com.example.pr_cbs.MainActivity
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.EventStorage
import kotlinx.android.synthetic.main.event_fragment.*
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.pr_cbs.AsyncTasks.LoadAllEventMFNsAsyncTask
import com.example.pr_cbs.AsyncTasks.LoadMoreEventRecordsAsyncTask
import com.example.pr_cbs.AsyncTasks.LoadAllActualEventsAsyncTask
import com.example.pr_cbs.EventFilterActivity
import com.example.pr_cbs.RecordStorage.EventRecord
import java.text.FieldPosition


class EventFragment : Fragment(), LoadAllActualEventsAsyncTask.LoadAllActualEventsFinished,
    LoadAllEventMFNsAsyncTask.LoadAllMFNsEventsFinished,
    LoadMoreEventRecordsAsyncTask.LoadMoreEventRecordsFinished {

    private lateinit var searchEventAdapter: EventAdapterNew
    private lateinit var mSearchLine: EditText
    lateinit var mContext: Context
    var savedDate: String = ""
    private var actualEvents: Boolean = true
    private var isDataLoading = false


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
        inflater.inflate(R.layout.event_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchEventAdapter = EventAdapterNew(this@EventFragment.context)
        mContext = this@EventFragment.context!!
        mSearchLine = view.findViewById(R.id.eT_event_search_line)

        if (MainActivity.checkSharedPreferenceAvailability("event_update_date",
                this@EventFragment.context!!
            ))
            savedDate = MainActivity.getFromSharedPreferences("this@EventFragment.context", this@EventFragment.context!!)


        //проверка адаптера на наличие элеметов
        if (searchEventAdapter.itemCount == 0) {
            this.loadAllActualEvents(false)
        } else this.showArrows()

        EventRecyclerView.layoutManager =
            LinearLayoutManager(this@EventFragment.context, RecyclerView.HORIZONTAL, false)

        EventRecyclerView.adapter = searchEventAdapter


        // Элемент RecyclerView по центру
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(EventRecyclerView)

        event_arrow_back.setOnClickListener {
            EventRecyclerView.smoothScrollToPosition(0)
        }

        event_arrow_forward.setOnClickListener {
            EventRecyclerView.smoothScrollToPosition(1)
        }


        EventRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val currentVisibleItem =
                    layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount


                event_arrow_forward.setOnClickListener {
                    EventRecyclerView.smoothScrollToPosition(layoutManager.findLastVisibleItemPosition() + 1)
                }

                event_arrow_back.setOnClickListener {
                    if (layoutManager.findLastVisibleItemPosition() != 0)
                        EventRecyclerView.smoothScrollToPosition(layoutManager.findLastVisibleItemPosition() - 1)
                }


                if (!actualEvents) {
                    if (!isDataLoading) {
                        if (currentVisibleItem == totalItemCount - 1) {
                            if (EventStorage.Instance().canLoadMore())
                                isDataLoading = true
                            LoadMoreEventRecordsAsyncTask(
                                this@EventFragment,
                                currentVisibleItem
                            ) { this@EventFragment.showEventProgressBar() }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                        }
                    }
                }
            }
        })


        event_filter_icon.setOnClickListener {
            val intent = Intent(context, EventFilterActivity::class.java)
            startActivityForResult(intent, 2)
        }


        event_reload_icon.setOnClickListener {
            actualEvents = true
            this.loadAllActualEvents(false)
        }


        event_search_icon.setOnClickListener {
            actualEvents = false
            this.loadEventsList()
        }
    }


    override fun allActualEventsLoaded(resCode: Int) {


        hideEventProgressBar()

        when (resCode) {
            0 -> showArrows()
            1 -> eventNotFound()
            2 -> serverError()
            3 -> noInternetConnection()

        }
    }

    override fun allEventMFNsLoaded(resCode: Int) {

        hideEventProgressBar()

        when (resCode) {
            0 -> showArrows()
            1 -> eventNotFound()
            2 -> serverError()
            3 -> noInternetConnection()

        }
    }


    override fun moreEventsLoaded(position: Int) {
        hideEventProgressBar()
        val size = EventStorage.Instance().localEventRecords.size

        for (i in position + 1..size) {
            searchEventAdapter.notifyItemChanged(i)
        }
        isDataLoading = false

    }


    private fun showArrows() {
        cl_event_arrows_block.visibility = VISIBLE
    }


    private fun noInternetConnection() {
        CL_event_fragment_no_internet_block.visibility = VISIBLE
    }


    private fun serverError() {
        CL_event_fragment_server_error_block.visibility = VISIBLE
    }

    private fun eventNotFound() {
        CL_event_fragment_not_found_block.visibility = VISIBLE
        val errorText =
            "К сожалению, по запросу\n\"${eT_event_search_line.text}\"\nничего не найдено"
        event_found_error_text.text = errorText

    }


    private fun preparingForSearching() {
        CL_event_fragment_no_internet_block.visibility = INVISIBLE
        CL_event_fragment_server_error_block.visibility = INVISIBLE
        CL_event_fragment_not_found_block.visibility = INVISIBLE
        cl_event_arrows_block.visibility = INVISIBLE
    }

    private fun showEventProgressBar() {
        event_ProgressBar_block.visibility = VISIBLE
    }

    private fun hideEventProgressBar() {
        event_ProgressBar_block.visibility = INVISIBLE
    }


    private fun loadEventsList() {
        showEventProgressBar()
        preparingForSearching()
        val internetConnection = (activity as MainActivity).isNetworkConnected()
        LoadAllEventMFNsAsyncTask(
            this@EventFragment,
            mSearchLine.text.toString(),
            { searchEventAdapter.notifyDataSetChanged() },
            internetConnection
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

    }

    private fun loadAllActualEvents(reload: Boolean) {
        preparingForSearching()
        val internetConnection = (activity as MainActivity).isNetworkConnected()
        this@EventFragment.context?.let {
            LoadAllActualEventsAsyncTask(
                this@EventFragment,
                it,
                reload,
                { searchEventAdapter.notifyDataSetChanged() },
                { this@EventFragment.showEventProgressBar() },
                internetConnection
            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (data == null) return
        when (requestCode) {
            //  1 ->  mSearchCombination = data.getStringExtra("name")
            2 -> {
//
            }
        }

    }


}


