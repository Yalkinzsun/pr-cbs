package com.example.pr_cbs.Adapters;

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pr_cbs.R
import com.example.pr_cbs.RecordStorage.LibraryRecord
import kotlinx.android.synthetic.main.activity_about_notification.view.*
import kotlinx.android.synthetic.main.adapter_library_item.view.*
import org.jsoup.select.Evaluator
import java.util.*


class LibraryAdapter(private val context: Context?, private val libList: ArrayList<LibraryRecord>) :
    RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(context).inflate(R.layout.adapter_library_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return libList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lib = libList[position]
        holder.setData(lib, position)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var currentLib: LibraryRecord? = null
        var currentPosition: Int = 0

//        init {
//            itemView.setOnClickListener {
//                val intent = Intent(context, MoreAboutLibrary::class.java)
//                context!!.startActivity(intent)
//            }
//        }

        @SuppressLint("SetTextI18n")
        fun setData(lib: LibraryRecord?, pos: Int) {


            itemView.lib_name.text = lib!!.name
            itemView.lib_address.text = lib.address
            itemView.lib_phone_1st.text = lib.phone_1st

            if (lib.phone_2nd != "null") {
                itemView.lib_phone_2nd.text = lib.phone_2nd
                itemView.lib_phone_2nd_block.visibility = VISIBLE
            }

            if (lib.mail != "null") itemView.lib_mail.text = lib.mail
            else itemView.lib_mail_block.visibility = GONE

            if (context != null) {
                Glide.with(context).load(lib.img_file).into( itemView.lib_image)
            }




//            itemView.lib_mon.text = lib.monday_working_hours
//            itemView.lib_tue.text = lib.tuesday_working_hours
//            itemView.lib_wen.text = lib.wednesday_working_hours
//            itemView.lib_thu.text = lib.thursday_working_hours
//            itemView.lib_fri.text = lib.friday_working_hours
//            itemView.lib_sat.text = lib.saturday_working_hours
//            itemView.lib_sun.text = lib.sunday_working_hours

//            if (lib.monday_working_hours == "выходной") itemView.lib_mon.setTextColor(Color.RED)
//            if (lib.tuesday_working_hours == "выходной") itemView.lib_tue.setTextColor(Color.RED)
//            if (lib.wednesday_working_hours == "выходной") itemView.lib_wen.setTextColor(Color.RED)
//            if (lib.thursday_working_hours == "выходной") itemView.lib_thu.setTextColor(Color.RED)
//            if (lib.friday_working_hours== "выходной") itemView.lib_fri.setTextColor(Color.RED)
//            if (lib.saturday_working_hours == "выходной") itemView.lib_sat.setTextColor(Color.RED)
//            if (lib.sunday_working_hours == "выходной") itemView.lib_sun.setTextColor(Color.RED)


            val c = Calendar.getInstance()

            val name = itemView.findViewById<TextView>(R.id.lib_name)

            when (c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())) {
                "понедельник" -> {

                    if (lib.monday_working_hours == "выходной") {
                        itemView.lib_today_working_hours.text =
                            "закрыта "
                        itemView.lib_today_working_hours.setTextColor(Color.RED)
                    } else {
                        itemView.lib_today_working_hours.text =
                            "открыта: " + lib.monday_working_hours
                        itemView.lib_today_working_hours.setTextColor(Color.parseColor("#43d143"))
                    }
                }


                "вторник" -> {
                    if (lib.tuesday_working_hours == "выходной") {
                        itemView.lib_today_working_hours.text =
                            "закрыта "
                        itemView.lib_today_working_hours.setTextColor(Color.RED)
                    } else {
                        itemView.lib_today_working_hours.text =
                            "открыта: " + lib.tuesday_working_hours
                        itemView.lib_today_working_hours.setTextColor(Color.parseColor("#43d143"))
                    }
                }


                "среда" -> {
                    if (lib.wednesday_working_hours == "выходной") {
                        itemView.lib_today_working_hours.text =
                            "закрыта "
                        itemView.lib_today_working_hours.setTextColor(Color.RED)
                    } else {
                        itemView.lib_today_working_hours.text =
                            "открыта: " + lib.wednesday_working_hours
                        itemView.lib_today_working_hours.setTextColor(Color.parseColor("#43d143"))
                    }
                }


                "четверг" -> {
                    if (lib.thursday_working_hours == "выходной") {
                        itemView.lib_today_working_hours.text =
                            "закрыта "
                        itemView.lib_today_working_hours.setTextColor(Color.RED)
                    } else {
                        itemView.lib_today_working_hours.text =
                            "открыта: " + lib.thursday_working_hours
                        itemView.lib_today_working_hours.setTextColor(Color.parseColor("#43d143"))
                    }
                }


                "пятница" -> {
                    if (lib.friday_working_hours == "выходной") {
                        itemView.lib_today_working_hours.text =
                            "закрыта "
                        itemView.lib_today_working_hours.setTextColor(Color.RED)
                    } else {
                        itemView.lib_today_working_hours.text =
                            "открыта: " + lib.friday_working_hours
                        itemView.lib_today_working_hours.setTextColor(Color.parseColor("#43d143"))
                    }
                }


                "суббота" -> {
                    if (lib.saturday_working_hours == "выходной") {
                        itemView.lib_today_working_hours.text =
                            "закрыта "
                        itemView.lib_today_working_hours.setTextColor(Color.RED)
                    } else {
                        itemView.lib_today_working_hours.text =
                            "открыта: " + lib.saturday_working_hours
                        itemView.lib_today_working_hours.setTextColor(Color.parseColor("#43d143"))
                    }
                }


                "воскресенье" -> {
                    if (lib.sunday_working_hours == "выходной") {

                        itemView.lib_today_working_hours.text =
                            "закрыта"
                        itemView.lib_today_working_hours.setTextColor(Color.RED)

                    } else {
                        itemView.lib_today_working_hours.text =
                            "открыта: " + lib.sunday_working_hours
                        itemView.lib_today_working_hours.setTextColor(Color.parseColor("#43d143"))
                    }
                }

            }


            this.currentLib = lib
            this.currentPosition = pos
        }

    }
}
