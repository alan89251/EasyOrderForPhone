package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.google_map_utils

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */

/**
 * Utility class for performing get request of Google API
 */
class GoogleAPIGetRequestClient {
    fun sendGetRequest(url: URL): JSONObject {
        // Open the connection
        val httpsURLConnection = url.openConnection() as HttpsURLConnection
        httpsURLConnection.doInput = true
        httpsURLConnection.connect()
        // Get response
        val responseStream = httpsURLConnection.getInputStream()
        val responseReader = BufferedReader(InputStreamReader(responseStream))
        var tempStr: String?
        val stringBuffer = StringBuffer()
        tempStr = responseReader.readLine()
        while (tempStr != null) {
            stringBuffer.append(tempStr)
            tempStr = responseReader.readLine()
        }
        val response = stringBuffer.toString()
        responseStream.close()

        return JSONObject(response)
    }
}