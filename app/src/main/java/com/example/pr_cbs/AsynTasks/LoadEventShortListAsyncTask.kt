package com.example.pr_cbs.AsynTasks

import android.content.Context
import android.os.AsyncTask
import com.example.pr_cbs.RecordStorage.EventStorage

class LoadEventShortListAsyncTask(
    private val callback: EventsATFinished,
    var context: Context,
    var downloadLatestBooksFromDatabase: Boolean,
    private var onNoResultsFound: () -> Unit

) : AsyncTask<Unit, Unit, Unit>() {

    private var hasResult: Boolean = true

    override fun onPreExecute() {
        super.onPreExecute()
        EventStorage.Instance().clear()

    }

    override fun doInBackground(vararg p0: Unit?) {
        this.hasResult = EventStorage.Instance().loadAllActualEvents(context, downloadLatestBooksFromDatabase )
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        if (!this.hasResult) {
            onNoResultsFound()
        } else {
            //TODO
        }

        callback.afterLastATFinished()
    }

    interface EventsATFinished {
        fun afterLastATFinished()
    }
}