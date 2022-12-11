package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_price

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceRepository

class PhonePriceViewModel(
    // dependency injection of PhonePriceRepository
    private val phonePriceRepository: PhonePriceRepository
) : ViewModel()
{
    val price = MutableLiveData(0.0)
    var priceQueryResult: LiveData<PhonePriceModel>? = null

    fun getPhonePrice(productId: Int, storageCapacity: String): LiveData<PhonePriceModel>? {
        return phonePriceRepository.getPhonePrice(productId, storageCapacity)
    }
}