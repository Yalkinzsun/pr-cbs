package com.example.pr_cbs.AsyncTasks


import android.os.AsyncTask
import com.example.pr_cbs.RecordStorage.BookStorage


class LoadMoreBookRecordsAsyncTask(
    private val callback: LoadMoreBookRecordsFinished,
    var position: Int,
    private var showProgressBar: () -> Unit

) : AsyncTask<Unit, Unit, Unit>() {


    override fun onPreExecute() {
        super.onPreExecute()
       showProgressBar()
    }

    override fun doInBackground(vararg p0: Unit?) {
        BookStorage.Instance().loadNextPage(position)


    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        callback.moreBooksLoaded(position)
    }

    interface LoadMoreBookRecordsFinished {
        fun moreBooksLoaded(position: Int)
    }
}
