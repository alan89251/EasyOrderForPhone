package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.event_handler

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.google_map_utils.GoogleAPIGetRequestClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatterBuilder

/**
 * Listener for the click event of a store marker
 */
class StoreMarkerClickListener : GoogleMap.OnMarkerClickListener{
    // callback that support finding the place id by marker id
    private val getPlaceIdByMarkerId: (String) -> String
    // do not use resource to save this config because it is highly couple to this class
    private val fieldsOfStoreDetail = "geometry%2Cname%2Cformatted_address%2Cformatted_phone_number%2Cwebsite%2Ccurrent_opening_hours%2Cphoto"
    private val placeDetailApiUrl: String
    private val placePhotoApiUrl: String
    private val apiKey: String
    private val storePhotoMaxHeight: Int
    private val map: GoogleMap
    private val infoWindowView: View

    constructor(getPlaceIdByMarkerId: (String) -> String,
                placeDetailApiUrl: String,
                placePhotoApiUrl: String,
                apiKey: String,
                storePhotoMaxHeight: Int,
                infoWindowView: View,
                map: GoogleMap) {
        this.getPlaceIdByMarkerId = getPlaceIdByMarkerId
        this.placeDetailApiUrl = placeDetailApiUrl
        this.placePhotoApiUrl = placePhotoApiUrl
        this.apiKey = apiKey
        this.storePhotoMaxHeight = storePhotoMaxHeight
        this.infoWindowView = infoWindowView
        this.map = map
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val placeId = getPlaceIdByMarkerId(marker.id)
        val urlStr = placeDetailApiUrl + "?" +
                "place_id" + "=" + placeId + "&" +
                "key" + "=" + apiKey + "&" +
                "fields" + "=" + fieldsOfStoreDetail
        CoroutineScope(Dispatchers.IO).launch {
            // Search the detail of the store by Google PlaceDetail API
            val response = GoogleAPIGetRequestClient()
                .sendGetRequest(URL(urlStr))

            withContext(Dispatchers.Main) {
                onReceivedStoreDetail(response, marker)
            }
        }

        return true
    }

    /**
     * process the store detail responded from the Google Place Detail API
     * If photo reference is exist in the response, download it by Google Place Photo API
     * @param responseOfStoreDetail response of the store detail from the Google PlaceDetail API
     */
    private fun onReceivedStoreDetail(responseOfStoreDetail: JSONObject, marker: Marker) {
        try {
            // parse store detail from API response
            val storeDetail = responseOfStoreDetail.getJSONObject("result")
            val name = storeDetail.getString("name")
            val address = storeDetail.getString("formatted_address")
            val phoneNumber = storeDetail.optString("formatted_phone_number") ?: "" // optional
            val website = storeDetail.optString("website") ?: "" // optional
            val openingHour = parseOpeningHourOfCurrentDay(storeDetail) // optional
            val photoRef = parsePhotoRef(storeDetail) // optional, photo reference to get photo by Google API

            // download the store photo
            CoroutineScope(Dispatchers.IO).launch {
                val photo = if (photoRef != "") {
                    downloadStorePhoto(photoRef)
                }
                else {
                    null
                }

                withContext(Dispatchers.Main) {
                    // set and show the info window
                    showInfoWindow(name,
                        address,
                        phoneNumber,
                        website,
                        openingHour,
                        photo,
                        marker
                    )
                }
            }
        }
        catch (e: JSONException) {
            Log.e("Store detail responded from API is not expected", e.toString())
        }
        catch (e: Exception) {
            Log.e("Fail to parse the responded store detail", e.toString())
        }
    }

    /**
     * Set the content of the info window and then show it
     * Also, center the map camera on the marker
     */
    private fun showInfoWindow(name: String,
                               address: String,
                               phoneNumber: String,
                               website: String,
                               openingHour: String,
                               photo: Bitmap?,
                               marker: Marker) {
        setInfoWindowView(name,
            address,
            phoneNumber,
            website,
            openingHour,
            photo
        )
        map.moveCamera(CameraUpdateFactory.newLatLng(marker.position))
        marker.showInfoWindow()
    }

    /**
     * Download the store photo by Google Photo API
     */
    private fun downloadStorePhoto(photoRef: String): Bitmap? {
        val urlStr = placePhotoApiUrl + "?" +
                "maxheight" + "=" + storePhotoMaxHeight.toString() + "&" +
                "photo_reference" + "=" + photoRef + "&" +
                "key" + "=" + apiKey
        val url = URL(urlStr)
        return downloadImage(url)
    }

    /**
     * Download an image by Google Photo API
     */
    private fun downloadImage(url: URL): Bitmap? {
        try {
            return BitmapFactory.decodeStream(url.openConnection().getInputStream())
        }
        catch(e: Exception) {
            Log.e("Download store photo fail", e.toString())
            return null
        }
    }

    /**
     * Set the contents in the info window
     */
    private fun setInfoWindowView(name: String,
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

    /**
     * parse opening hour of current date from store detail
     * opening hour is an optional field in the API
     * if it doesn't exist, return empty string
     */
    private fun parseOpeningHourOfCurrentDay(storeDetail: JSONObject): String {
        // if opening hour info doesn't exist, return
        val openingDetail = storeDetail.optJSONObject("current_opening_hours")
        if (openingDetail == null) {
            return ""
        }
        val openingPeriods = openingDetail.optJSONArray("periods")
        if (openingPeriods == null) {
            return ""
        }

        val currentDate = LocalDate.now() // Get the string of current date
            .format(DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd")
                .toFormatter())
        // parse the opening hour of current date
        var openingHour = ""
        for (i in 0..openingPeriods.length()-1) {
            val openingHourDetail = openingPeriods.getJSONObject(i)
                .getJSONObject("open")
            val date = openingHourDetail.getString("date")
            if (date.equals(currentDate)) {
                openingHour = openingHourDetail.getString("time")
                break
            }
        }
        // format the opening hour
        if (openingHour != "") {
            openingHour = LocalTime.from(
                DateTimeFormatterBuilder()
                    .appendPattern("HHmm")
                    .toFormatter()
                    .parse(openingHour)
            ).format(
                DateTimeFormatterBuilder()
                    .appendPattern("KK:mm a")
                    .toFormatter()
            )
        }
        return openingHour
    }

    /**
     * parse photo reference from store detail
     * Get one photo reference only
     * If photo doesn't exist, return empty string
     */
    private fun parsePhotoRef(storeDetail: JSONObject): String {
        val photos = storeDetail.optJSONArray("photos")
        return if (photos == null || photos.length() <= 0) {
            ""
        }
        else {
            photos.getJSONObject(0)
                .getString("photo_reference")
        }
    }
}