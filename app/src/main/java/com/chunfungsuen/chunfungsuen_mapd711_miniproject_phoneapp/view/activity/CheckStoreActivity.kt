package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity

import android.content.Intent
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.logic.DownloadRouteToStoreLogic
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.logic.DownloadStoreInfoLogic
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.logic.GetDeviceLocationLogic
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.logic.MenuOnSelectHandler
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class CheckStoreActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var deviceLocation: Location
    private lateinit var storePlaceId: String
    private lateinit var menuOnSelectHandler: MenuOnSelectHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_store)

        menuOnSelectHandler = MenuOnSelectHandler(null, this)

        storePlaceId = intent.getStringExtra("storePlaceId")!!

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.check_store_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        GetDeviceLocationLogic(
            getSystemService(LOCATION_SERVICE) as LocationManager,
            this
        )
            .requestLocation(::onReceievedDeviceLocation)
    }

    /**
     * Center the map camera on the location of the device
     * Add a circle on the map to indicate the location of the device
     * Send a Google PlaceDetail API request to get the store detail
     */
    private fun onReceievedDeviceLocation(location: Location) {
        deviceLocation = location
        configMapContentByDeviceLocation()
        DownloadStoreInfoLogic(
            ::onDownloadStoreInfoCompleted,
            resources.getString(R.string.google_place_detail_api_url),
            resources.getString(R.string.google_place_photo_api_url),
            resources.getString(R.string.google_api_key),
            resources.getInteger(R.integer.store_photo_max_height)
        )
            .asyncDownloadStoreInfo(storePlaceId)
    }

    /**
     * 1. Display a marker for the store on the map
     * 2. Download the route from device location to the selected store
     */
    private fun onDownloadStoreInfoCompleted(storeInfo: DownloadStoreInfoLogic.StoreInfo) {
        mMap.addMarker(
            MarkerOptions()
                .position(storeInfo.latLng!!)
        )

        DownloadRouteToStoreLogic(
            ::onReceivedRouteToStore,
            resources.getString(R.string.google_directions_api_url),
            resources.getString(R.string.google_api_key)
        )
            .asyncDownloadRouteToStore(storeInfo.latLng!!, LatLng(deviceLocation.latitude, deviceLocation.longitude))
    }

    /**
     * Plot the route to the store on the map
     */
    private fun onReceivedRouteToStore(route: List<LatLng>) {
        mMap.addPolyline(
            PolylineOptions()
            .addAll(route))
    }

    /**
     * Set the displaying region of the map to the surrounding of the current location
     * Add a circle on the map to indicate the location of the device
     */
    private fun configMapContentByDeviceLocation() {
        val mapObj = LatLng(deviceLocation.latitude, deviceLocation.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapObj, 15f))
        mMap.addCircle(
            CircleOptions()
                .center(mapObj)
                .radius(30.0)
                .fillColor(resources.getColor(R.color.locationIndicatorFillColor))
                .strokeColor(resources.getColor(R.color.locationIndicatorStrokeColor)))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.phone_order_service_menu, menu)
        return true
    }

    /**
     * Navigate to the activity of the selected menu item
     * Do nothing if the user select the current activity
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent? = menuOnSelectHandler.createIntent(item)
        if (intent != null) {
            startActivity(intent)
        }

        return true
    }
}