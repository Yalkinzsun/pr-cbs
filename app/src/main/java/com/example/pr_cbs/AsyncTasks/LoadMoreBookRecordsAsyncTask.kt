package com.example.pr_cbs.AsyncTasks

import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import com.example.pr_cbs.RecordStorage.BookStorage


class LoadMoreBookRecordsAsyncTask(
    private val callback: LoadMoreBookRecordsFinished,
    var position: Int,
    var mProgressBar2: ProgressBar
   // private var notifyDataSetChanged: () -> Unit
) : AsyncTask<Unit, Unit, Unit>() {


    override fun onPreExecute() {
        super.onPreExecute()
        mProgressBar2.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg p0: Unit?) {
        BookStorage.Instance().loadNextPage(position)


    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
     //   notifyDataSetChanged()
        mProgressBar2.visibility = View.GONE

        callback.moreBooksLoaded(position)
    }

    interface LoadMoreBookRecordsFinished {
        fun moreBooksLoaded(position: Int)
    }
}
