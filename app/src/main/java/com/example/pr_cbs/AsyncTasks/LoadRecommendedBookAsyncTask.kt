package com.example.pr_cbs.AsyncTasks

import android.content.Context
import android.os.AsyncTask
import com.example.pr_cbs.RecordStorage.LatestBooksStorage

class LoadRecommendedBookAsyncTask(
    private val callback: RecommendedATFinished,
    var context: Context,
    var downloadLatestBooksFromDatabase: Boolean

) : AsyncTask<Unit, Unit, Unit>() {


    private var hasResult: Boolean = true

    override fun onPreExecute() {
        super.onPreExecute()

        LatestBooksStorage.Instance().clear()
    }

    override fun doInBackground(vararg p0: Unit?) {
        this.hasResult = LatestBooksStorage.Instance()
            .downloadRecommendedBooks(downloadLatestBooksFromDatabase, context)

    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)


        callback.fromSecondATtoThird(this.hasResult)
    }

    interface RecommendedATFinished {
        fun fromSecondATtoThird(recommendedResult: Boolean)
    }
}