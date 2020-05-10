package com.example.pr_cbs

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.*
import android.widget.Toast
import com.example.pr_cbs.AsyncTasks.LoadEventShortListAsyncTask
import com.example.pr_cbs.AsyncTasks.LoadLatestBookAsyncTask
import com.example.pr_cbs.AsyncTasks.LoadRecommendedBookAsyncTask
import com.example.pr_cbs.Database.DBHelper
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class SplashScreen : AppCompatActivity(), LoadLatestBookAsyncTask.LatestATFinished,
    LoadRecommendedBookAsyncTask.RecommendedATFinished,
    LoadEventShortListAsyncTask.EventsATFinished {

    private val APP_PREFERENCES = "pref_settings"
    private val APP_PREFERENCES_LATEST_BOOK_UPDATE_DATE = "latest_book_update_date"
    private val APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE = "recommended_book_update_date"
    private var latestError: Boolean = true
    private var recommendedError: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val versionName = "версия ${BuildConfig.VERSION_NAME}"
        splash_screen_program_version.text = versionName



        if (isNetworkConnected(this)) {
            startDownloading()
        } else {
            splash_screen_downloading.visibility = INVISIBLE
            splash_screen_no_internet_text.visibility = VISIBLE
            val dataCheck = isAnyData()

            if (dataCheck[0] != 0 || dataCheck[1] != 0 || dataCheck[2] != 0) {
                startDownloading()

            } else {

                splash_screen_reload_button.visibility = VISIBLE
                splash_screen_progressBar.visibility = INVISIBLE
            }
        }


        splash_screen_reload_button.setOnClickListener {
            if (isNetworkConnected(this)) {
                splash_screen_progressBar.visibility = VISIBLE
                splash_screen_downloading.visibility = VISIBLE
                startDownloading()
                splash_screen_reload_button.visibility = GONE
                splash_screen_no_internet_text.visibility = GONE

            } else Toast.makeText(
                this@SplashScreen,
                "Подключитесь к Интернету",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun isAnyData(): IntArray {
        val list = IntArray(3) { 0 }

        val dbHelper = DBHelper(this)
        val database = dbHelper.writableDatabase


        val cursorFirst =
            database.query(DBHelper.TABLE_LATEST, null, null, null, null, null, null)

        if (cursorFirst != null && cursorFirst.count > 0) list[0] = 1

        cursorFirst.close()

        val cursorSecond = database.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, null)

        if (cursorSecond != null && cursorSecond.count > 0) list[1] = 1

        cursorSecond.close()

        val cursorThird =
            database.query(DBHelper.TABLE_RECOMMENDED, null, null, null, null, null, null)

        if (cursorThird != null && cursorThird.count > 0) list[2] = 1

        cursorThird.close()

        return list
    }


    private fun startDownloading() {
        val sPref = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if (sPref.contains(APP_PREFERENCES_LATEST_BOOK_UPDATE_DATE)) {

            val latestBookUpdateDate =
                sPref.getString(APP_PREFERENCES_LATEST_BOOK_UPDATE_DATE, "").toString()

            if (isTheDateRelevant(latestBookUpdateDate)) {

                val dbHelper = DBHelper(this)
                val database = dbHelper.writableDatabase

                val cursor =
                    database.query(DBHelper.TABLE_LATEST, null, null, null, null, null, null)

                if (cursor != null && cursor.count > 0) {
                    cursor.close()
                    this.loadLatestBook(true)

                } else this.loadLatestBook(false)

            } else this.loadLatestBook(false)

        } else this.loadLatestBook(false)

    }


    override fun fromFirstATtoSecond(latestResult: Boolean) {
        latestError = latestResult

        val sPref = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if (sPref.contains(APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE)) {

            val recommendedBookUpdateDate =
                sPref.getString(APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE, "").toString()

            if (isTheDateRelevant(recommendedBookUpdateDate)) {

                val dbHelper = DBHelper(this)
                val database = dbHelper.writableDatabase

                val cursor =
                    database.query(
                        DBHelper.TABLE_RECOMMENDED,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    )

                if (cursor != null && cursor.count > 0) {
                    cursor.close()
                    this.loadRecommendedBooks(true)

                } else this.loadRecommendedBooks(false)

            } else this.loadRecommendedBooks(false)

        } else this.loadRecommendedBooks(false)
    }


    override fun fromSecondATtoThird(recommendedResult: Boolean) {
        recommendedError = recommendedResult
        this.loadEventShortList()
    }


    override fun afterLastATFinished(hasResult: Int) {
        var status = 0
        if (!isNetworkConnected(this)) status = 1
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("connection_status", status)
        intent.putExtra("event_storage_downloading_error", hasResult)
        intent.putExtra("latest_storage_downloading_error", latestError)
        intent.putExtra("recommended_storage_downloading_error", recommendedError)
        startActivity(intent)

    }


    private fun isTheDateRelevant(savedDate: String): Boolean {

        if (!isNetworkConnected(this)) return true
        val isTheDateRelevant: Boolean
        val currentDate = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().time)
        val currentDateLatest: Date? = SimpleDateFormat("dd.MM.yyyy HH:mm").parse(currentDate)
        val savedDateLatest: Date? = SimpleDateFormat("dd.MM.yyyy HH:mm").parse(savedDate)
        val diff = currentDateLatest!!.time - savedDateLatest!!.time
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        isTheDateRelevant = hours < 24
        return isTheDateRelevant
    }


    private fun isNetworkConnected(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = true
                }
            }
        } else {
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                if (activeNetwork.getType() === ConnectivityManager.TYPE_WIFI) {
                    result = true
                } else if (activeNetwork.getType() === ConnectivityManager.TYPE_MOBILE) {
                    result = true
                }
            }
        }

        return result
    }


    private fun loadLatestBook(canDownloadLatestBooksFromDatabase: Boolean) {

        LoadLatestBookAsyncTask(
            this,
            this,
            canDownloadLatestBooksFromDatabase
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

    }


    private fun loadEventShortList() {
      val internetConnection = isNetworkConnected(this)
        LoadEventShortListAsyncTask(
            this,
            this,
            false,
            internetConnection
            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

    }


    private fun loadRecommendedBooks(canDownloadRecommendedBooksFromDatabase: Boolean) {
        LoadRecommendedBookAsyncTask(
            this,
            this,
            canDownloadRecommendedBooksFromDatabase
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

    }


}
