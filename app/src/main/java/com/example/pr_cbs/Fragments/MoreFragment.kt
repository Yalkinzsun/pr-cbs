package com.example.pr_cbs.Fragments

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService


import androidx.fragment.app.Fragment
import com.example.pr_cbs.MainActivity
import com.example.pr_cbs.MapActivity
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.BookStorage
import kotlinx.android.synthetic.main.more_fragment.*
import java.util.*


class MoreFragment : Fragment() {

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

        testButton.setOnClickListener {
            val intent = Intent(this@MoreFragment.context, MapActivity::class.java)
            startActivity(intent)
        }


    }



}
