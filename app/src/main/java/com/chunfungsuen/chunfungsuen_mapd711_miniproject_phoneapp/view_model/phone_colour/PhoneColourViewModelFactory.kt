package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_colour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour.PhoneColourRepository

class PhoneColourViewModelFactory(val phoneColourRepository: PhoneColourRepository)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PhoneColourRepository::class.java)
            .newInstance(phoneColourRepository)
    }
}