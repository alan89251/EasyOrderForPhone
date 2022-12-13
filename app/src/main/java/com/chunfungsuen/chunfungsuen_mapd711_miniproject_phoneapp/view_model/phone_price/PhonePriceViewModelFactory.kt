package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_price

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceRepository

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class PhonePriceViewModelFactory(val phonePriceRepository: PhonePriceRepository)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PhonePriceRepository::class.java)
            .newInstance(phonePriceRepository)
    }
}