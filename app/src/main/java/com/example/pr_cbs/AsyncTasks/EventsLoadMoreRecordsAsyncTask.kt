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


class EventsLoadMoreRecordsAsyncTask(
    private var mProgressBar2: ProgressBar,
    private var notifyDataSetChanged: () -> Unit
) : AsyncTask<Unit, Unit, Unit>() {


    override fun onPreExecute() {
        super.onPreExecute()
        mProgressBar2.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg p0: Unit?) {
        EventStorage.Instance().loadNextPage()
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
        notifyDataSetChanged()
        mProgressBar2.visibility = View.GONE
    }
}