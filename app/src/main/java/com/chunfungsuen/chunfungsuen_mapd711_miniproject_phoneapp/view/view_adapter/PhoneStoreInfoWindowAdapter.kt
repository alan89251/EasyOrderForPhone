package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter

import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class PhoneStoreInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
    private val contentView: View // layout of the info window

    constructor(contentView: View) {
        this.contentView = contentView
    }

    override fun getInfoContents(marker: Marker): View? {
        return contentView
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}