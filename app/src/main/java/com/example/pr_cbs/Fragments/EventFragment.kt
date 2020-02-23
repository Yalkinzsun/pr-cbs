package com.example.pr_cbs.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
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
import androidx.recyclerview.widget.SnapHelper
import com.example.pr_cbs.EventFilterActivity
import java.text.SimpleDateFormat
import java.util.*


class EventFragment : Fragment() {

    private lateinit var searchEventAdapter: EventAdapterNew
    private lateinit var mEventProgressBar: ProgressBar
    private lateinit var mEventProgressBar2: ProgressBar
    private lateinit var mErrorImage: ImageView
    private lateinit var mErrorText: TextView
    private lateinit var mSearchLine: EditText
    lateinit var mContext: Context
    lateinit var sPref: SharedPreferences
    var savedDate: String = ""
    private val APP_PREFERENCES = "pref_settings"
    private val APP_PREFERENCES_EVENT_UPDATE_DATE = "event_update_date"
    private var actualEvents: Boolean = true


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


        mEventProgressBar = view.findViewById(R.id.eventProgressBar1)
        mEventProgressBar2 = view.findViewById(R.id.eventProgressBar2)

        searchEventAdapter = EventAdapterNew(this@EventFragment.context)
        mContext = this@EventFragment.context!!

        mErrorImage = view.findViewById(R.id.event_error_image)
        mErrorText = view.findViewById(R.id.event_error_text)
        mSearchLine = view.findViewById(R.id.eT_event_search_line)

        sPref = this.activity!!.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        if (sPref.contains(APP_PREFERENCES_EVENT_UPDATE_DATE))
            savedDate = sPref.getString(APP_PREFERENCES_EVENT_UPDATE_DATE, "").toString()


        //проверка адаптера на наличие элеметов
        if (searchEventAdapter.itemCount == 0) {
            this.loadAllActualEvents(false)
        }


        EventRecyclerView.layoutManager =
            LinearLayoutManager(this@EventFragment.context, RecyclerView.HORIZONTAL, false)

        EventRecyclerView.adapter = searchEventAdapter


        // Элемент RecyclerView по центру
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(EventRecyclerView)

        // val layoutManager = recyclerView.layoutManager as LinearLayoutManager

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
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition()

                event_arrow_forward.setOnClickListener {
                    EventRecyclerView.smoothScrollToPosition(layoutManager.findLastVisibleItemPosition() + 1)
                }

                event_arrow_back.setOnClickListener {
                    if (layoutManager.findLastVisibleItemPosition() != 0)
                        EventRecyclerView.smoothScrollToPosition(layoutManager.findLastVisibleItemPosition() - 1)
                }

                if (!actualEvents) {
                    if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                        if (EventStorage.Instance().canLoadMore())
                            EventsLoadMoreRecordsAsyncTask(
                                mEventProgressBar2
                            ) { searchEventAdapter.notifyDataSetChanged() }.executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR
                            )

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
            this.loadAllActualEvents(true)
        }


        event_search_icon.setOnClickListener {
            actualEvents = false
            this.loadEventsList()
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


    private fun loadAllActualEvents(reload: Boolean) {
        this@EventFragment.context?.let {
            LoadAllActualEventsAsyncTask(
                it,
                reload,
                mEventProgressBar,
                { searchEventAdapter.notifyDataSetChanged() },
                {
                  //TODO неясность
                    Toast.makeText(context, "К сожалению, ничего не найдено", Toast.LENGTH_LONG)
                        .show()

                }
            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }


    private fun loadEventsList() {

        EventsLoadMFNsAsyncTask(
            mSearchLine.text.toString(),
            mErrorImage,
            mErrorText,
            mEventProgressBar,
            { searchEventAdapter.notifyDataSetChanged() },
            {
                mErrorImage.visibility = VISIBLE
                mErrorText.visibility = VISIBLE
                val searchQuery = this.mSearchLine.text.toString()
                val errorText =  "к сожалению, по запросу\n\"$searchQuery\" \nничего не найдено"
                mErrorText.text = errorText
            }
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

    }


    class LoadAllActualEventsAsyncTask(
        var mContext: Context,
        var reload: Boolean,
        var mProgressBar: ProgressBar,
        private var notifyDataSetChanged: () -> Unit,
        private var onNoResultsFound: () -> Unit
    ) : AsyncTask<Unit, Unit, Unit>() {

        private var hasResult: Boolean = true


        override fun onPreExecute() {
            super.onPreExecute()
            mProgressBar.visibility = VISIBLE

            EventStorage.Instance().clear()
            notifyDataSetChanged()
        }

        override fun doInBackground(vararg p0: Unit?) {


            this.hasResult = true

            // TODO разобраться с этим
            if (hasResult)
                EventStorage.Instance().loadAllActualEvents(mContext, reload)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            mProgressBar.visibility = GONE
            notifyDataSetChanged()


            //  mFound.visibility = VISIBLE
            //  mNumberOfBooks.visibility = VISIBLE

            if (!this.hasResult) {
                onNoResultsFound()
            } else {
//                mSearchImage.visibility = INVISIBLE
//                mSearchTextView.visibility = INVISIBLE
            }
        }
    }


    class EventsLoadMFNsAsyncTask(
        var mSearchLine: String,
        var mErrorImage: ImageView,
        var mErrorText: TextView,
        var mProgressBar: ProgressBar,
        private var notifyDataSetChanged: () -> Unit,
        private var onNoResultsFound: () -> Unit
    ) : AsyncTask<Unit, Unit, Unit>() {

        private var hasResult: Boolean = true


        override fun onPreExecute() {
            super.onPreExecute()

            mErrorImage.visibility = INVISIBLE
            mErrorText.visibility = INVISIBLE
            mProgressBar.visibility = VISIBLE

            EventStorage.Instance().clear()
            notifyDataSetChanged()
        }

        override fun doInBackground(vararg p0: Unit?) {

            this.hasResult = EventStorage.Instance()
                .fetchMFNsByQuery(mSearchLine)

            EventStorage.Instance().loadNextPage()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            mProgressBar.visibility = GONE
            notifyDataSetChanged()

            if (!this.hasResult) {
                onNoResultsFound()
            } else {
                mErrorImage.visibility = INVISIBLE
                mErrorText.visibility = INVISIBLE
            }
        }
    }

    class EventsLoadMoreRecordsAsyncTask(
        private var mProgressBar2: ProgressBar,
        private var notifyDataSetChanged: () -> Unit
    ) : AsyncTask<Unit, Unit, Unit>() {


        override fun onPreExecute() {
            super.onPreExecute()
            mProgressBar2.visibility = VISIBLE
        }

        override fun doInBackground(vararg p0: Unit?) {
            EventStorage.Instance().loadNextPage()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            notifyDataSetChanged()
            mProgressBar2.visibility = GONE
        }
    }
}


