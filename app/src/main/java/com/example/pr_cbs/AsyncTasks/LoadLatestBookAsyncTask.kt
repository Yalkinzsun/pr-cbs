package com.example.pr_cbs.AsyncTasks

import android.content.Context
import android.os.AsyncTask
import com.example.pr_cbs.RecordStorage.RecommendedBooksStorage

class LoadLatestBookAsyncTask(
    private val callback: LatestATFinished,
    var context: Context,
    var downloadLatestBooksFromDatabase: Boolean


) : AsyncTask<Unit, Unit, Unit>() {

    private var hasResult: Boolean = true

    override fun onPreExecute() {
        super.onPreExecute()
        RecommendedBooksStorage.Instance().clear()
    }

    override fun doInBackground(vararg p0: Unit?) {
        this.hasResult =
            RecommendedBooksStorage.Instance()
                .downloadLatestBooks(downloadLatestBooksFromDatabase, context)

    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)


        callback.fromFirstATtoSecond(hasResult)
    }

    interface LatestATFinished {
        fun fromFirstATtoSecond(latestResult: Boolean)
    }
}
