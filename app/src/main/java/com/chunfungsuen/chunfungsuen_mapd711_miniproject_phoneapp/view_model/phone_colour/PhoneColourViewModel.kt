package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_colour

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour.PhoneColourModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour.PhoneColourRepository

class PhoneColourViewModel(
    // dependency injection of PhoneColourRepository
    private val phoneColourRepository: PhoneColourRepository
) : ViewModel()
{
    fun getPhoneColours(productId: Int): LiveData<List<PhoneColourModel>>? {
        return phoneColourRepository.getPhoneColours(productId)
    }
}