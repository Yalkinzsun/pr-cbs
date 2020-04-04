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

class LoadAllEventMFNsAsyncTask(
    private val callback: LoadAllMFNsEventsFinished,
    var mSearchLine: String,
    private var notifyDataSetChanged: () -> Unit,
    private val internetConnection: Boolean
) : AsyncTask<Unit, Unit, Unit>() {

    private var hasResult: Int = 0


    override fun onPreExecute() {
        super.onPreExecute()

        EventStorage.Instance().clear()
        notifyDataSetChanged()
    }

    override fun doInBackground(vararg p0: Unit?) {

        this.hasResult = EventStorage.Instance().fetchMFNsByQuery(mSearchLine, internetConnection)

        EventStorage.Instance().loadNextPage(0)
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        callback.allEventMFNsLoaded(hasResult)

    }

    interface LoadAllMFNsEventsFinished {
        fun allEventMFNsLoaded(resCode: Int)
    }
}