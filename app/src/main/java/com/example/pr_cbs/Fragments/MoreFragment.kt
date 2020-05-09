package com.example.pr_cbs.Fragments


import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pr_cbs.MainActivity
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.LibraryRecord
import kotlinx.android.synthetic.main.more_fragment.*
import com.example.pr_cbs.Adapters.LibraryAdapter as LibraryAdapter


class MoreFragment : Fragment() {


    private lateinit var searchLibraryAdapter: LibraryAdapter
    private var libsList: ArrayList<LibraryRecord> = arrayListOf()

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).supportActionBar!!.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.more_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchLibraryAdapter = LibraryAdapter(this@MoreFragment.context, libsList)

        more_lib_recycler.layoutManager =
            LinearLayoutManager(this@MoreFragment.context, RecyclerView.HORIZONTAL, false)

        more_lib_recycler.adapter = searchLibraryAdapter

        this.loadLibInfo()


        // Элемент RecyclerView по центру
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(more_lib_recycler)


//        testButton.setOnClickListener {
//            val intent = Intent(this@MoreFragment.context, MapActivity::class.java)
//            startActivity(intent)
//        }


    }


    private fun loadLibInfo() {

        libsList.add(
            LibraryRecord(
                "Центральная районная библиотека им. А. С. Пушкина",
                "Каменоостровский пр., 36/73",
                "null",
                "8 (812) 346-26-07",
                "8 (812) 417-34-25",
                "выходной",
                "11:00 - 20:00",
                "11:00 - 21:00",
                "11:00 - 20:00",
                "11:00 - 20:00",
                "11:00 - 20:00",
                "10:00 - 18:00"

            )
        )
        libsList.add(
            LibraryRecord(
                "Центральная районная детская библиотека",
                "Большой проспект. П.С., 65 (2-ой этаж)",
                "petr-crdb@mail.ru",
                "8 (812) 232-41-29",
                "null",
                "выходной",
                "10:00 - 20:00",
                "10:00 - 20:00",
                "10:00 - 20:00",
                "10:00 - 20:00",
                "10:00 - 18:00",
                "10:00 - 18:00"

            )
        )
        libsList.add(
            LibraryRecord(
                "Бибилиотека Кировских островов",
                "Кемская ул., 8/3",
                "krest.bibl@mail.ru",
                "8 (812) 235-01-63",
                "null",
                "выходной",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "12:00 - 20:00"

            )
        )

        libsList.add(
            LibraryRecord(
                "Бибилиотека им. Б. А. Лавренева",
                "наб. р. Карповки, 28",
                "bibllav@yandex.ru",
                "8 (812) 346-09-17",
                "null",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "выходной"

            )
        )
        libsList.add(
            LibraryRecord(
                "Бибилиотека им. В. И. Ленин",
                "ул. Воскова, 2",
                "leblenina-reader@mail.ru",
                "8 (812) 232-40-62",
                "null",
                "09:00 - 09:00",
                "09:00 - 21:00",
                "09:00 - 021:00",
                "09:00 - 21:00",
                "11:00 - 21:00",
                "11:00 - 21:00",
                "11:00 - 21:00"

            )
        )
        libsList.add(
            LibraryRecord(
                "Юношеская библиотека им. А. П. Гайдара",
                "Большой пр. П.С., 18А (4-й этаж)",
                "gaidara-spb@mail.ru",
                "8 (812) 235-35-96",
                "null",
                "11:00 - 20:00",
                "11:00 - 20:00",
                "11:00 - 20:00",
                "11:00 - 20:00",
                "11:00 - 20:00",
                "11:00 - 19:00",
                "выходной"

            )
        )
        libsList.add(
            LibraryRecord(
                "2-я детская библиотека",
                "Татарский пер., 1",
                "2child.library@mail.ru",
                "8 (812) 232-21-64",
                "null",
                "10:00 - 20:00",
                "10:00 - 20:00",
                "10:00 - 20:00",
                "10:00 - 20:00",
                "10:00 - 20:00",
                "10:00 - 18:00",
                "выходной"

            )
        )
        libsList.add(
            LibraryRecord(
                "3-я районная библиотека",
                "Троицкая пл. П.С., 1",
                "troickaya1@mail.ru",
                "8 (812) 232-58-36",
                "null",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "11:00 - 19:00",
                "выходной",
                "11:00 - 19:00",
                "11:00 - 19:00"

            )
        )
        searchLibraryAdapter.notifyDataSetChanged()

    }


}
