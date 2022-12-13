package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour

import androidx.lifecycle.LiveData

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class PhoneColourRepository(private val phoneColourDao: PhoneColourDao) {
    fun getPhoneColours(productId: Int): LiveData<List<PhoneColourModel>>? {
        return phoneColourDao.getPhoneColours(productId)
    }
}