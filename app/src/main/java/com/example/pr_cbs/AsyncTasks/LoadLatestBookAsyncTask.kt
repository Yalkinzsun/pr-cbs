package com.example.pr_cbs.AsyncTasks

import android.content.Context
import android.os.AsyncTask
import com.example.pr_cbs.RecordStorage.LatestBookStorage

class LoadLatestBookAsyncTask(
    private val callback: LatestATFinished,
    var context: Context,
    var downloadLatestBooksFromDatabase: Boolean,
    private var onNoResultsFound: () -> Unit

) : AsyncTask<Unit, Unit, Unit>() {

    private var hasResult: Boolean = true

    override fun onPreExecute() {
        super.onPreExecute()
        LatestBookStorage.Instance().clear()
    }

    override fun doInBackground(vararg p0: Unit?) {
        this.hasResult =
            LatestBookStorage.Instance()
                .downloadLatestBooks(downloadLatestBooksFromDatabase, context)

    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        if (!this.hasResult) {
            onNoResultsFound()
        } else {
            //TODO

        }

        callback.fromFirstATtoSecond()
    }

    interface LatestATFinished {
        fun fromFirstATtoSecond()
    }
}