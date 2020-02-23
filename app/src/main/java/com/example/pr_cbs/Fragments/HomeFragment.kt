package com.example.pr_cbs.Fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.cardview.widget.CardView

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.InfiniteCarouselTransformer
import com.example.pr_cbs.R
import com.example.pr_cbs.Adapters.RecommendedBooksAdapter
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter.wrap
import kotlinx.android.synthetic.main.home_fragment.*
import com.example.pr_cbs.Adapters.CarouselLatestAdapter
import com.example.pr_cbs.Database.DBHelper
import com.example.pr_cbs.RecordStorage.EventRecord
import com.example.pr_cbs.RecordStorage.EventStorage
import com.example.pr_cbs.RecordStorage.LatestBookStorage
import com.example.pr_cbs.RecordStorage.RecommendedBookStorage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(),
    DiscreteScrollView.OnItemChangedListener<CarouselLatestAdapter.MyViewHolder> {


    private lateinit var mInfiniteScrollWrapper: InfiniteScrollAdapter<*>

    private lateinit var mLatestAdapterMain: CarouselLatestAdapter
    private lateinit var mRecommendedAdapterMain: RecommendedBooksAdapter

    private lateinit var mProgressBar1: ProgressBar
    private lateinit var mProgressBar2: ProgressBar
    private lateinit var mProgressBar3: ProgressBar
    private lateinit var mCardView: CardView


    private val APP_PREFERENCES = "pref_settings"
    private val APP_PREFERENCES_LATEST_BOOK_UPDATE_DATE = "latest_book_update_date"
    private val APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE = "recommended_book_update_date"
    private val APP_PREFERENCES_EVENT_UPDATE_DATE = "event_update_date"


    override fun onCurrentItemChanged(p0: CarouselLatestAdapter.MyViewHolder?, p1: Int) {
        val realPosition = mInfiniteScrollWrapper.realCurrentPosition
        Log.v("HomeTag", ("onCurrentItemChanged $realPosition"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.home_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mProgressBar1 = view.findViewById(R.id.progressBarLatest)
        mProgressBar2 = view.findViewById(R.id.progressBarRecommended)
        mProgressBar3 = view.findViewById(R.id.progressBarHomeEvent)
        mCardView = view.findViewById(R.id.home_event_card_view)


        //Рекомендуемые книги
        this.mRecommendedAdapterMain = RecommendedBooksAdapter(this@HomeFragment.context)
        recyclerViewRecommendedBooks.layoutManager =
            LinearLayoutManager(this@HomeFragment.context, RecyclerView.HORIZONTAL, false)
        recyclerViewRecommendedBooks.adapter = RecommendedBooksAdapter(this@HomeFragment.context)

        // Элемент RecyclerView по центру
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewRecommendedBooks)

        //Последние поступления
        this.mLatestAdapterMain = CarouselLatestAdapter(this@HomeFragment.context)

        // Бесконечное прокручивание
        this.mInfiniteScrollWrapper = wrap(this.mLatestAdapterMain)
        infinite_carousel.adapter = mInfiniteScrollWrapper

        // Трансформирование элемента
        this.infinite_carousel.setItemTransformer(InfiniteCarouselTransformer())

        this.infinite_carousel.addOnItemChangedListener(this)


        val currentDate = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().time)
        val sPref = this.activity!!.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        if (sPref.contains(APP_PREFERENCES_LATEST_BOOK_UPDATE_DATE)) {

            val latestBookUpdateDate =
                sPref.getString(APP_PREFERENCES_LATEST_BOOK_UPDATE_DATE, "").toString()

            if (isTheDateRelevant(currentDate, latestBookUpdateDate)) {

                this.loadLatestBook(true)

            } else this.loadLatestBook(false)

        } else this.loadLatestBook(false)


        if (sPref.contains(APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE)) {

            val recommendedBookUpdateDate =
                sPref.getString(APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE, "").toString()

            if (isTheDateRelevant(currentDate, recommendedBookUpdateDate)) {

                this.loadRecommendedBooks(true)

            } else this.loadRecommendedBooks(false)

        } else this.loadRecommendedBooks(false)


        if (sPref.contains(APP_PREFERENCES_EVENT_UPDATE_DATE)) {

            val eventUpdateDate =
                sPref.getString(APP_PREFERENCES_EVENT_UPDATE_DATE, "").toString()

            if (isTheDateRelevant(currentDate, eventUpdateDate)) {


                val dbHelper = DBHelper(this@HomeFragment.context)
                val database = dbHelper.writableDatabase


                val cursor =
                    database.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, null)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToPosition(0)

                    val startDateIndex = cursor.getColumnIndex(DBHelper.KEY_START_DATE)
                    val dayMonth = cursor.getString(startDateIndex)
                    home_day_number_1.text = getDay(dayMonth).toString()
                    home_weekday_1.text = getMonth(dayMonth)

                    val nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME)
                    home_event_name_1.text = cursor.getString(nameIndex)

                    val locationIndex = cursor.getColumnIndex(DBHelper.KEY_LIBRARY)
                    home_event_location_1.text = cursor.getString(locationIndex)

                    cursor.moveToPosition(1)

                    val startDateIndex1 = cursor.getColumnIndex(DBHelper.KEY_START_DATE)
                    val dayMonth1 = cursor.getString(startDateIndex1)
                    home_day_number_2.text = getDay(dayMonth1).toString()
                    home_weekday_2.text = getMonth(dayMonth1)

                    val nameIndex1 = cursor.getColumnIndex(DBHelper.KEY_NAME)
                    home_event_name_2.text = cursor.getString(nameIndex1)

                    val locationIndex1 = cursor.getColumnIndex(DBHelper.KEY_LIBRARY)
                    home_event_location_2.text = cursor.getString(locationIndex1)


                   // this.loadEventShortList(true)

                } else this.loadEventShortList(false)

                cursor.close()

            } else this.loadEventShortList(false)

        } else this.loadEventShortList(false)

    }

    private fun getDay(line: String): Int {
        val parts = line.split(".")
        return parts[0].toInt()
    }

    private fun getMonth(line: String): String {
        var mountDecryption = "янв"
        val parts = line.split(".")

        when (parts[1].toInt()) {
            2 -> mountDecryption = "фев"
            3 -> mountDecryption = "мар"
            4 -> mountDecryption = "апр"
            5 -> mountDecryption = "мая"
            6 -> mountDecryption = "июн"
            7 -> mountDecryption = "июл"
            8 -> mountDecryption = "авг"
            9 -> mountDecryption = "сен"
            10 -> mountDecryption = "окт"
            11 -> mountDecryption = "ноя"
            12 -> mountDecryption = "дек"
        }
        return mountDecryption
    }


    private fun isTheDateRelevant(currentDate: String, savedDate: String): Boolean {
        val isTheDateRelevant: Boolean
        val currentDateLatest: Date? = SimpleDateFormat("dd.MM.yyyy HH:mm").parse(currentDate)
        val savedDateLatest: Date? = SimpleDateFormat("dd.MM.yyyy HH:mm").parse(savedDate)
        val diff = currentDateLatest!!.time - savedDateLatest!!.time
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        isTheDateRelevant = hours < 24
        return isTheDateRelevant
    }


    private fun loadLatestBook(canDownloadLatestBooksFromDatabase: Boolean) {
        activity?.applicationContext?.let {
            LoadLatestBookAsyncTask(
                it,
                canDownloadLatestBooksFromDatabase,
                mProgressBar1,
                { mLatestAdapterMain.notifyDataSetChanged() },
                {
                    //TODO
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    private fun loadEventShortList(canDownloadEventsFromDatabase: Boolean) {
        activity?.applicationContext?.let {
            LoadEventShortListAsyncTask(
                it,
                mCardView,
                canDownloadEventsFromDatabase,
                mProgressBar3,
                {
                    //TODO
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    // https://ru.stackoverflow.com/questions/373288/asynctask-%d0%ba%d0%b0%d0%ba-%d0%be%d1%82%d0%b4%d0%b5%d0%bb%d1%8c%d0%bd%d1%8b%d0%b9-%d0%ba%d0%bb%d0%b0%d1%81%d1%81/373522#373522
    class LoadEventShortListAsyncTask(
        var context: Context,
        var cardView: CardView,
        var downloadLatestBooksFromDatabase: Boolean,
        var mProgressBar: ProgressBar,
        private var onNoResultsFound: () -> Unit

    ) : AsyncTask<Unit, Unit, Unit>() {

        private var hasResult: Boolean = true

        override fun onPreExecute() {
            super.onPreExecute()

            mProgressBar.visibility = VISIBLE
            cardView.visibility = GONE

            EventStorage.Instance().clear()

        }

        override fun doInBackground(vararg p0: Unit?) {
            this.hasResult = EventStorage.Instance().loadAllActualEvents(context, true)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            mProgressBar.visibility = GONE
            cardView.visibility = VISIBLE

            if (!this.hasResult) {
                onNoResultsFound()
            } else {
                //TODO
            }
        }
    }


    class LoadLatestBookAsyncTask(
        var context: Context,
        var downloadLatestBooksFromDatabase: Boolean,
        var mProgressBar1: ProgressBar,
        private var notifyDataSetChanged: () -> Unit,
        private var onNoResultsFound: () -> Unit

    ) : AsyncTask<Unit, Unit, Unit>() {

        private var hasResult: Boolean = true

        override fun onPreExecute() {
            super.onPreExecute()

            mProgressBar1.visibility = VISIBLE

            LatestBookStorage.Instance().clear()
            notifyDataSetChanged()
        }

        override fun doInBackground(vararg p0: Unit?) {
            this.hasResult =
                LatestBookStorage.Instance()
                    .downloadLatestBooks(downloadLatestBooksFromDatabase, context)

        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            mProgressBar1.visibility = GONE
            notifyDataSetChanged()

            if (!this.hasResult) {
                onNoResultsFound()
            } else {
                //TODO
            }
        }
    }


    private fun loadRecommendedBooks(canDownloadRecommendedBooksFromDatabase: Boolean) {

        activity?.applicationContext?.let {
            LoadRecommendedBookAsyncTask(
                it,
                canDownloadRecommendedBooksFromDatabase,
                mProgressBar2,
                { mLatestAdapterMain.notifyDataSetChanged() },
                {
                    //TODO
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    class LoadRecommendedBookAsyncTask(
        var context: Context,
        var downloadLatestBooksFromDatabase: Boolean,
        var mProgressBar2: ProgressBar,
        private var notifyDataSetChanged: () -> Unit,
        private var onNoResultsFound: () -> Unit

    ) : AsyncTask<Unit, Unit, Unit>() {

        private var hasResult: Boolean = true

        override fun onPreExecute() {
            super.onPreExecute()

            mProgressBar2.visibility = VISIBLE

            RecommendedBookStorage.Instance().clear()
            notifyDataSetChanged()
        }

        override fun doInBackground(vararg p0: Unit?) {
            this.hasResult = RecommendedBookStorage.Instance()
                .downloadRecommendedBooks(downloadLatestBooksFromDatabase, context)

        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            mProgressBar2.visibility = GONE
            notifyDataSetChanged()

            if (!this.hasResult) {
                onNoResultsFound()
            } else {
                //TODO
            }
        }
    }

}



