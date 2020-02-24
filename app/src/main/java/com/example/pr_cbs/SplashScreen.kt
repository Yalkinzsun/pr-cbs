package com.example.pr_cbs

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pr_cbs.AsynTasks.LoadEventShortListAsyncTask
import com.example.pr_cbs.AsynTasks.LoadLatestBookAsyncTask
import com.example.pr_cbs.AsynTasks.LoadRecommendedBookAsyncTask
import com.example.pr_cbs.Database.DBHelper
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SplashScreen : AppCompatActivity(), LoadLatestBookAsyncTask.LatestATFinished,
    LoadRecommendedBookAsyncTask.RecommendedATFinished,
    LoadEventShortListAsyncTask.EventsATFinished {

    private val APP_PREFERENCES = "pref_settings"
    private val APP_PREFERENCES_LATEST_BOOK_UPDATE_DATE = "latest_book_update_date"
    private val APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE = "recommended_book_update_date"
    private val APP_PREFERENCES_EVENT_UPDATE_DATE = "event_update_date"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        button_pop.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val currentDate = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().time)
        val sPref = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        if (sPref.contains(APP_PREFERENCES_LATEST_BOOK_UPDATE_DATE)) {

            val latestBookUpdateDate =
                sPref.getString(APP_PREFERENCES_LATEST_BOOK_UPDATE_DATE, "").toString()

            if (isTheDateRelevant(currentDate, latestBookUpdateDate)) {

                val dbHelper = DBHelper(this)
                val database = dbHelper.writableDatabase

                val cursor =
                    database.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, null)

                if (cursor != null && cursor.count > 0) {
                    cursor.close()
                    this.loadLatestBook(true)

                } else this.loadLatestBook(false)

            } else this.loadLatestBook(false)

        } else this.loadLatestBook(false)


    }

    override fun fromFirstATtoSecond() {
        val currentDate = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().time)
        val sPref = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if (sPref.contains(APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE)) {

            val recommendedBookUpdateDate =
                sPref.getString(APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE, "").toString()

            if (isTheDateRelevant(currentDate, recommendedBookUpdateDate)) {

                val dbHelper = DBHelper(this)
                val database = dbHelper.writableDatabase

                val cursor =
                    database.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, null)

                if (cursor != null && cursor.count > 0) {
                    cursor.close()
                    this.loadRecommendedBooks(true)

                } else this.loadRecommendedBooks(false)

            } else this.loadRecommendedBooks(false)

        } else this.loadRecommendedBooks(false)
    }

    override fun fromSecondATtoThird() {
      this.loadEventShortList(false)
    }

    override fun afterLastATFinished() {
        val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

        LoadLatestBookAsyncTask(
            this,
            this,
            canDownloadLatestBooksFromDatabase,
            {}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

    }

    private fun loadEventShortList(reload: Boolean) {

        LoadEventShortListAsyncTask(
            this,
            this,
            reload,
            {}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

    }

    private fun loadRecommendedBooks(canDownloadRecommendedBooksFromDatabase: Boolean) {
        LoadRecommendedBookAsyncTask(
            this,
            this,
            canDownloadRecommendedBooksFromDatabase,
            {}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

    }


}
