package com.example.pr_cbs.AsyncTasks

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.pr_cbs.RecordStorage.BookStorage
import com.example.pr_cbs.RecordStorage.EventStorage

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

        mErrorImage.visibility = View.INVISIBLE
        mErrorText.visibility = View.INVISIBLE
        mProgressBar.visibility = View.VISIBLE

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

        mProgressBar.visibility = View.GONE
        notifyDataSetChanged()

        if (!this.hasResult) {
            onNoResultsFound()
        } else {
            mErrorImage.visibility = View.INVISIBLE
            mErrorText.visibility = View.INVISIBLE
        }
    }
}