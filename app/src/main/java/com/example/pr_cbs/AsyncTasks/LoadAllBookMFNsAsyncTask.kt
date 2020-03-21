package com.example.pr_cbs.AsyncTasks

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.pr_cbs.RecordStorage.BookStorage

class LoadAllBookMFNsAsyncTask(
    private val callback: LoadAllBookMFNsFinished,
    var advanced_flag: Boolean,
    var mSearchCombination: String,
    private var getSearchText: () -> String,
    private var notifyDataSetChanged: () -> Unit,
    private var preparingForSearch: () -> Unit,
    var internetConnection: Boolean

) : AsyncTask<Unit, Unit, Unit>() {

    private var hasResult: Int = 0
    private lateinit var bookCount: String

    override fun onPreExecute() {
        super.onPreExecute()

        BookStorage.Instance().clear()
        notifyDataSetChanged()
        preparingForSearch()
    }

    override fun doInBackground(vararg p0: Unit?) {
        if (mSearchCombination != "") {

            if (advanced_flag) this.hasResult =
                BookStorage.Instance().fetchMFNsByQueryAdvanced(mSearchCombination, internetConnection)
            else this.hasResult =
                BookStorage.Instance().fetchMFNsByQuery(mSearchCombination, internetConnection)

        } else this.hasResult = BookStorage.Instance().fetchMFNsByQuery(this.getSearchText(), internetConnection)
        bookCount = BookStorage.Instance().mfNsCount.toString()
        BookStorage.Instance().loadNextPage(0)
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        notifyDataSetChanged()

        callback.allBookMFNsLoaded(hasResult, bookCount)

    }

    interface LoadAllBookMFNsFinished {
        fun allBookMFNsLoaded(resCode: Int, bookCount: String)
    }

}