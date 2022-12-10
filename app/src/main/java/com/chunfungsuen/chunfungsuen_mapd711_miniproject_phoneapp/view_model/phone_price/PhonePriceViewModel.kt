package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_price

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceRepository

class PhonePriceViewModel(
    // dependency injection of PhonePriceRepository
    private val phonePriceRepository: PhonePriceRepository
) : ViewModel()
{
    fun getPhonePrice(productId: Int, storageCapacityId: Int): LiveData<PhonePriceModel>? {
        return phonePriceRepository.getPhonePrice(productId, storageCapacityId)
    }
}