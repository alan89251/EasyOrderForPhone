package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price

import androidx.lifecycle.LiveData

class PhonePriceRepository(private val phonePriceDao: PhonePriceDao)
{
    fun getPhonePrice(productId: Int, storageCapacityId: Int): LiveData<PhonePriceModel>? {
        return phonePriceDao.getPhonePrice(productId, storageCapacityId)
    }
}