package com.example.pr_cbs.AsyncTasks

import android.content.Context
import android.os.AsyncTask
import com.example.pr_cbs.RecordStorage.EventStorage

class LoadEventShortListAsyncTask(
    private val callback: EventsATFinished,
    var context: Context,
    var downloadLatestBooksFromDatabase: Boolean,
    var internetConnection: Boolean

) : AsyncTask<Unit, Unit, Unit>() {

    private var hasResult: Int = 0

    override fun onPreExecute() {
        super.onPreExecute()
        EventStorage.Instance().clear()

    }

    override fun doInBackground(vararg p0: Unit?) {
        this.hasResult = EventStorage.Instance()
            .loadAllActualEvents(context, downloadLatestBooksFromDatabase,internetConnection)
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        callback.afterLastATFinished(hasResult)
    }

    interface EventsATFinished {
        fun afterLastATFinished(hasResult: Int)
    }
}