package com.example.pr_cbs

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : FragmentActivity(), OnMapReadyCallback {

    lateinit var map: GoogleMap
    lateinit var library: LatLng
    lateinit var libraryName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


        library = giveCoordinates(0)



        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        arrow_back_map_activity.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap!!
        val zoomLevel = 14.0.toFloat()
        map.addMarker(MarkerOptions().position(library).title(libraryName))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(library, zoomLevel))

    }

    private fun giveCoordinates(lib: Int): LatLng {
        val place: LatLng
        when (lib) {
            0 -> {
                place = LatLng(59.939317, 30.315534)
                libraryName = "Первая бибилиотека"
            }
            1 -> place = LatLng(15.5, 30.315534)


            else -> {
                place = LatLng(50.0, 50.0)
                libraryName = "Произошла ошибка"
            }
        }

        textView_activity_map.text = libraryName

        return place
    }


}
