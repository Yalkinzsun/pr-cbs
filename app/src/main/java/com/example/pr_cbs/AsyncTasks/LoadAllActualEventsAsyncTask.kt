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

class LoadAllActualEventsAsyncTask(
    var mContext: Context,
    var reload: Boolean,
    var mProgressBar: ProgressBar,
    private var notifyDataSetChanged: () -> Unit,
    private var onNoResultsFound: () -> Unit
) : AsyncTask<Unit, Unit, Unit>() {

    private var hasResult: Boolean = true


    override fun onPreExecute() {
        super.onPreExecute()
        mProgressBar.visibility = View.VISIBLE

        EventStorage.Instance().clear()
        notifyDataSetChanged()
    }

    override fun doInBackground(vararg p0: Unit?) {


        this.hasResult = true

        // TODO разобраться с этим
        if (hasResult)
            EventStorage.Instance().loadAllActualEvents(mContext, reload)
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        mProgressBar.visibility = View.GONE
        notifyDataSetChanged()


        //  mFound.visibility = VISIBLE
        //  mNumberOfBooks.visibility = VISIBLE

        if (!this.hasResult) {
            onNoResultsFound()
        } else {
//                mSearchImage.visibility = INVISIBLE
//                mSearchTextView.visibility = INVISIBLE
        }
    }
}