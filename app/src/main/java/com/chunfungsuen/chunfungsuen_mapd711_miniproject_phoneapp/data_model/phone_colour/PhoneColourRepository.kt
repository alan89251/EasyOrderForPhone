package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour

import androidx.lifecycle.LiveData

class PhoneColourRepository(private val phoneColourDao: PhoneColourDao) {
    fun getPhoneColours(productId: Int): LiveData<List<PhoneColourModel>>? {
        return phoneColourDao.getPhoneColours(productId)
    }
}