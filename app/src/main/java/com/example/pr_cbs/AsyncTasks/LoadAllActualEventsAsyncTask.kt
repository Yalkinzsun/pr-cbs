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
    private val callback: LoadAllActualEventsFinished,
    var mContext: Context,
    var reload: Boolean,
    private var notifyDataSetChanged: () -> Unit,
    private var showProgressBar: () -> Unit,
    private val internetConnection: Boolean
) : AsyncTask<Unit, Unit, Unit>() {

    private var hasResult: Int = 0


    override fun onPreExecute() {
        super.onPreExecute()

        EventStorage.Instance().clear()
        notifyDataSetChanged()
        showProgressBar()
    }

    override fun doInBackground(vararg p0: Unit?) {

        this.hasResult = EventStorage.Instance().loadAllActualEvents(mContext, reload, internetConnection)
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
            notifyDataSetChanged()
            callback.allActualEventsLoaded(hasResult)
    }

    interface LoadAllActualEventsFinished {
        fun allActualEventsLoaded(resCode: Int)
    }

}



