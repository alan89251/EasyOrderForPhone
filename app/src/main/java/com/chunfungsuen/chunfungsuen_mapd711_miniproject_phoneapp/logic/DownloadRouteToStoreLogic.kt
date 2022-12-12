package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.logic

import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.google_map_utils.GoogleAPIGetRequestClient
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.google_map_utils.GoogleMapAPIPolylineDecoder
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class DownloadRouteToStoreLogic(
    private val onResult: (List<LatLng> , LatLngBounds) -> Unit, // args: route, bound
    private val apiUrl: String,
    private val apiKey: String
) {
    fun asyncDownloadRouteToStore(storeLocation: LatLng, deviceLocation: LatLng) {
        val urlStr = apiUrl + "?" +
                "origin" + "=" + String.format("%f%%2C%f", deviceLocation.latitude, deviceLocation.longitude) + "&" +
                "destination" + "=" + String.format("%f%%2C%f", storeLocation.latitude, storeLocation.longitude) + "&" +
                "key" + "=" + apiKey
        CoroutineScope(Dispatchers.IO).launch {
            val response = GoogleAPIGetRequestClient()
                .sendGetRequest(URL(urlStr))
            val route: List<LatLng> = parseRoute(response)
            val bound = parseBound(response)

            withContext(Dispatchers.Main) {
                onResult(route, bound)
            }
        }
    }

    private fun parseBound(responseFromApi: JSONObject): LatLngBounds {
        val bounds = responseFromApi.getJSONArray("routes")
            .getJSONObject(0)
            .getJSONObject("bounds")
        val northeast = bounds.getJSONObject("northeast")
        val northeastBound = LatLng(northeast.getDouble("lat"), northeast.getDouble("lng"))
        val southwest = bounds.getJSONObject("southwest")
        val southwestBound = LatLng(southwest.getDouble("lat"), southwest.getDouble("lng"))
        return LatLngBounds(southwestBound, northeastBound)
    }

    /**
     * parse route from the Google Directions API response
     */
    private fun parseRoute(responseFromApi: JSONObject): List<LatLng> {
        val steps = responseFromApi.getJSONArray("routes")
            .getJSONObject(0)
            .getJSONArray("legs")
            .getJSONObject(0)
            .getJSONArray("steps")
        val decoder = GoogleMapAPIPolylineDecoder()
        var route = ArrayList<LatLng>()
        val lastIdxOfSteps = steps.length() - 1
        for (i in 0 .. lastIdxOfSteps) {
            route.addAll(
                decoder.decode(
                    steps.getJSONObject(i)
                        .getJSONObject("polyline")
                        .getString("points")
                )
            )
        }

        return route
    }
}