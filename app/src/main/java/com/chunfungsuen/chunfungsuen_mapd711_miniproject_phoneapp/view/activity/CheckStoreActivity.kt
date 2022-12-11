package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class CheckStoreActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_store)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.check_store_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}