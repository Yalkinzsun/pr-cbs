package com.example.pr_cbs.AsyncTasks


import android.os.AsyncTask
import com.example.pr_cbs.RecordStorage.EventStorage


class LoadMoreEventRecordsAsyncTask(
    private val callback: LoadMoreEventRecordsFinished,
    var position: Int,
    private var showProgressBar: () -> Unit

) : AsyncTask<Unit, Unit, Unit>() {


    override fun onPreExecute() {
        super.onPreExecute()
        showProgressBar()
    }

    override fun doInBackground(vararg p0: Unit?) {
        EventStorage.Instance().loadNextPage(position)
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

      callback.moreEventsLoaded(position)
    }

    interface LoadMoreEventRecordsFinished {
        fun moreEventsLoaded(position: Int)
    }
}