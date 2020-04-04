package com.example.pr_cbs.AsyncTasks


import android.os.AsyncTask
import com.example.pr_cbs.RecordStorage.TakenBookStorage

class LoadTakenBooksAsyncTask(
    private val callback: LoadTakenBooksFinished,
    var ticketNumber: String,
    private val internetConnection: Boolean

) : AsyncTask<Unit, Unit, Unit>() {

    private var hasResult: Int = 0


    override fun onPreExecute() {
        super.onPreExecute()

        TakenBookStorage.Instance().clear()
    }

    override fun doInBackground(vararg p0: Unit?) {

        this.hasResult = TakenBookStorage.Instance().downloadAllTakenBooks(ticketNumber, internetConnection)

    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)


        callback.allTakenBooksLoaded(hasResult)

    }

    interface LoadTakenBooksFinished {
        fun allTakenBooksLoaded(resCode: Int)
    }

}


