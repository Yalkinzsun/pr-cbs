package com.example.pr_cbs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.activity_how_to_become_a_reader.*
import android.graphics.Matrix
import android.widget.ImageView
import android.R.attr.pivotY
import android.R.attr.pivotX
import android.R.attr.angle


class HowToBecomeAReaderActivity : AppCompatActivity() {

    var arrowDownPressed1: Boolean = false
    var arrowDownPressed2: Boolean = false
    var arrowDownPressed3: Boolean = false
    var arrowDownPressed4: Boolean = false
    var arrowDownPressed5: Boolean = false
    var arrowDownPressed6: Boolean = false
    var arrowDownPressed7: Boolean = false
    var arrowDownPressed8: Boolean = false
    var arrowDownPressed9: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_become_a_reader)


        setSupportActionBar(toolbar_how_to_become_a_reader)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Как стать читателем"



        iv_become_a_reader_1.setOnClickListener {
            if (this.arrowDownPressed1) {
                iv_become_a_reader_1.rotation = 360F
                rl_become_a_reader_1.visibility = GONE
                arrowDownPressed1 = false

            } else {
                rl_become_a_reader_1.visibility = VISIBLE
                iv_become_a_reader_1.rotation = 180F
                arrowDownPressed1 = true

            }

        }


        iv_become_a_reader_2.setOnClickListener {
            if (this.arrowDownPressed2) {
                iv_become_a_reader_2.rotation = 360F
                rl_become_a_reader_2.visibility = GONE
                arrowDownPressed2 = false

            } else {
                rl_become_a_reader_2.visibility = VISIBLE
                iv_become_a_reader_2.rotation = 180F
                arrowDownPressed2 = true

            }

        }

        iv_become_a_reader_3.setOnClickListener {
            if (this.arrowDownPressed3) {
                iv_become_a_reader_3.rotation = 360F
                rl_become_a_reader_3.visibility = GONE
                arrowDownPressed3 = false

            } else {
                rl_become_a_reader_3.visibility = VISIBLE
                iv_become_a_reader_3.rotation = 180F
                arrowDownPressed3 = true

            }

        }

        iv_become_a_reader_4.setOnClickListener {
            if (this.arrowDownPressed4) {
                iv_become_a_reader_4.rotation = 360F
                rl_become_a_reader_4.visibility = GONE
                arrowDownPressed4 = false

            } else {
                rl_become_a_reader_4.visibility = VISIBLE
                iv_become_a_reader_4.rotation = 180F
                arrowDownPressed4 = true

            }

        }

        iv_become_a_reader_5.setOnClickListener {
            if (this.arrowDownPressed5) {
                iv_become_a_reader_5.rotation = 360F
                rl_become_a_reader_5.visibility = GONE
                arrowDownPressed5 = false

            } else {
                rl_become_a_reader_5.visibility = VISIBLE
                iv_become_a_reader_5.rotation = 180F
                arrowDownPressed5 = true

            }

        }

        iv_become_a_reader_6.setOnClickListener {
            if (this.arrowDownPressed6) {
                iv_become_a_reader_6.rotation = 360F
                rl_become_a_reader_6.visibility = GONE
                arrowDownPressed6 = false

            } else {
                rl_become_a_reader_6.visibility = VISIBLE
                iv_become_a_reader_6.rotation = 180F
                arrowDownPressed6 = true

            }

        }

        iv_become_a_reader_7.setOnClickListener {
            if (this.arrowDownPressed7) {
                iv_become_a_reader_7.rotation = 360F
                rl_become_a_reader_7.visibility = GONE
                arrowDownPressed7 = false

            } else {
                rl_become_a_reader_7.visibility = VISIBLE
                iv_become_a_reader_7.rotation = 180F
                arrowDownPressed7= true

            }

        }

        iv_become_a_reader_8.setOnClickListener {
            if (this.arrowDownPressed8) {
                iv_become_a_reader_8.rotation = 360F
                rl_become_a_reader_8.visibility = GONE
                arrowDownPressed8 = false

            } else {
                rl_become_a_reader_8.visibility = VISIBLE
                iv_become_a_reader_8.rotation = 180F
                arrowDownPressed8 = true

            }

        }

        iv_become_a_reader_9.setOnClickListener {
            if (this.arrowDownPressed9) {
                iv_become_a_reader_9.rotation = 360F
                rl_become_a_reader_9.visibility = GONE
                arrowDownPressed9 = false

            } else {
                rl_become_a_reader_9.visibility = VISIBLE
                iv_become_a_reader_9.rotation = 180F
                arrowDownPressed9 = true

            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
