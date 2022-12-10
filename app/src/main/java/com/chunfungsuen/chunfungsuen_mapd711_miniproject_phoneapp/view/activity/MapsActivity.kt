package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.databinding.ActivityMapsBinding
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.google_map_utils.GoogleAPIGetRequestClient
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.logic.DownloadStoreInfoLogic
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter.PhoneStoreInfoWindowAdapter
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var isUpdateStoreInfoOnMap = true // control whether to update the store info shown on the map
    private var selectedBrand = "" // user selected brand of phone
    private var markerIdPlaceIdMap = HashMap<String, String>() // key: marker id, value: place id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedBrand = intent.getStringExtra("brand").toString()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInitStatusOfSwitchMapBtns() // Set the initial status of the switch map buttons

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Set the initial status of the switch map buttons
     */
    private fun setInitStatusOfSwitchMapBtns() {
        disableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_standard))
        enableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_satellite))
        enableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_Hybrid))
    }

    /**
     * 1. Config the info window adapter, marker click event handler
     * 2. Request the location of the device
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val infoWindowView = layoutInflater.inflate(R.layout.store_info_window, null)
        val phoneStoreInfoWindowAdapter = PhoneStoreInfoWindowAdapter(infoWindowView)
        mMap.setInfoWindowAdapter(phoneStoreInfoWindowAdapter)
        setMapOnMarkerClickListener(infoWindowView)

        requestLocation() // get the current location
    }

    /**
     * get place id by marker id
     * @param markerId marker id
     * @return place id
     */
    private fun getPlaceIdByMarkerId(markerId: String) : String {
        return markerIdPlaceIdMap[markerId] ?: ""
    }

    // listener to receive location
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            if (isUpdateStoreInfoOnMap) {
                isUpdateStoreInfoOnMap = false // only update the info once
                onReceievedDeviceLocation(p0)
            }
        }
    }

    /**
     * Send a request for location
     */
    private fun requestLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val ONE_HOUR_IN_MS = 3600000L // interval for location update, set to 1 hour to prevent frequent update
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ONE_HOUR_IN_MS, 0F, locationListener)
    }

    /**
     * Center the map camera on the location of the device
     * Add a circle on the map to indicate the location of the device
     * Send a Google API request to get the stores selling the user selected brand of phone
     */
    private fun onReceievedDeviceLocation(location: Location) {
        configMapContentByDeviceLocation(location)
        searchStoresSellingUserSelectedBrand(location)
    }

    /**
     * Search the stores selling the user selected brand of phone
     */
    private fun searchStoresSellingUserSelectedBrand(location: Location) {
        val latLngStr = String.format("%f%%2C%f", location.latitude, location.longitude)
        val urlStr = resources.getString(R.string.google_place_search_api_url) + "?" +
                "location" + "=" + latLngStr + "&" +
                "query" + "=" + String.format("stores%%2Cselling%%2C%s%%2Cphones", selectedBrand) + "&" +
                "radius" + "=" + resources.getInteger(R.integer.searching_radius_for_stores).toString() + "&" +
                "key" + "=" + resources.getString(R.string.google_api_key)
        CoroutineScope(Dispatchers.IO).launch {
            val response = GoogleAPIGetRequestClient()
                .sendGetRequest(URL(urlStr))

            withContext(Dispatchers.Main) {
                onReceivedStoreSearchingResult(response)
            }
        }
    }

    /**
     * Set the displaying region of the map to the surrounding of the current location
     * Add a circle on the map to indicate the location of the device
     */
    private fun configMapContentByDeviceLocation(location: Location) {
        val mapObj = LatLng(location.latitude, location.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapObj, 15f))
        mMap.addCircle(
            CircleOptions()
            .center(mapObj)
            .radius(30.0)
            .fillColor(resources.getColor(R.color.locationIndicatorFillColor))
            .strokeColor(resources.getColor(R.color.locationIndicatorStrokeColor)))
    }

    /**
     * Add markers on the map for the stores received
     * Also, save the relationship between the marker id and the store id
     * @param responseFromApi response that received from Google Place API
     */
    private fun onReceivedStoreSearchingResult(responseFromApi: JSONObject) {
        try {
            val stores = responseFromApi.getJSONArray("results")
            for (i in 0..stores.length()-1) {
                val store = stores.getJSONObject(i)
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(parseLatLng(store))
                )

                // save the relationship between the marker id and the store id
                markerIdPlaceIdMap[marker!!.id] = store.getString("place_id")
            }
        }
        catch (e: JSONException) {
            Log.e("Store info of phone brand responded from API is not expected", e.toString())
        }
        catch (e: Exception) {
            Log.e("Fail to parse store info of phone brand responded from API", e.toString())
        }
    }

    /**
     * Parse LatLng from a JSONObject of a store in the search result from Google Place API
     * @param store store in the search result from Google Place API
     */
    private fun parseLatLng(store: JSONObject) : LatLng {
        val location = store.getJSONObject("geometry")
            .getJSONObject("location")
        return LatLng(location.getDouble("lat"), location.getDouble("lng"))
    }

    /**
     * Switch map type to normal (standard)
     */
    fun onClickBtnSwitchToStandard(v: View) {
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        disableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_standard))
        enableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_satellite))
        enableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_Hybrid))
    }

    /**
     * Switch map type to satellite
     */
    fun onClickBtnSwitchToSatellite(v: View) {
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        enableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_standard))
        disableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_satellite))
        enableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_Hybrid))
    }

    /**
     * Switch map type to hybrid
     */
    fun onClickBtnSwitchToHybrid(v: View) {
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        enableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_standard))
        enableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_satellite))
        disableSwitchMapTypeBtn(findViewById<Button>(R.id.btn_switch_to_Hybrid))
    }

    /**
     * Enable a switch map type button and apply the related style on it
     */
    private fun enableSwitchMapTypeBtn(button: Button) {
        button.isEnabled = true
        button.setBackgroundColor(resources.getColor(R.color.btnSwitchMapTypeEnabledBgColor))
        button.setTextColor(resources.getColor(R.color.btnSwitchMapTypeEnabledTextColor))
    }

    /**
     * Disable a switch map type button and apply the related style on it
     */
    private fun disableSwitchMapTypeBtn(button: Button) {
        button.isEnabled = false
        button.setBackgroundColor(resources.getColor(R.color.btnSwitchMapTypeDisabledBgColor))
        button.setTextColor(resources.getColor(R.color.btnSwitchMapTypeDisabledTextColor))
    }

    /**
     * Set listener for the click event of a store marker
     */
    private fun setMapOnMarkerClickListener(infoWindowView: View) {
        mMap.setOnMarkerClickListener { marker ->
            DownloadStoreInfoLogic(
                {
                    showInfoWindow(it, marker, infoWindowView)
                },
                resources.getString(R.string.google_place_detail_api_url),
                resources.getString(R.string.google_place_photo_api_url),
                resources.getString(R.string.google_api_key),
                resources.getInteger(R.integer.store_photo_max_height)
            )
                .asyncDisplayStoreInfoWindow(getPlaceIdByMarkerId(marker.id))

            true
        }
    }

    /**
     * Set the content of the info window and then show it
     * Also, center the map camera on the marker
     */
    private fun showInfoWindow(storeInfo: DownloadStoreInfoLogic.StoreInfo,
                               marker: Marker,
                               infoWindowView: View
    ) {
        setInfoWindowView(
            infoWindowView,
            storeInfo.name,
            storeInfo.address,
            storeInfo.phoneNumber,
            storeInfo.website,
            storeInfo.openingHour,
            storeInfo.photo
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.position))
        marker.showInfoWindow()
    }

    /**
     * Set the contents in the info window
     */
    private fun setInfoWindowView(infoWindowView: View,
                                  name: String,
                                  address: String,
                                  phoneNumber: String,
                                  website: String,
                                  openingHour: String,
                                  photo: Bitmap?
    ) {
        if (photo != null) {
            infoWindowView.findViewById<ImageView>(R.id.store_photo).setImageBitmap(photo)
        }
        infoWindowView.findViewById<TextView>(R.id.store_name).text = name
        infoWindowView.findViewById<TextView>(R.id.store_address).text = address
        infoWindowView.findViewById<TextView>(R.id.store_phone_number).text = phoneNumber
        infoWindowView.findViewById<TextView>(R.id.store_opening_hour).text = "Opens at " +openingHour
        infoWindowView.findViewById<TextView>(R.id.store_website).text = website
    }
}