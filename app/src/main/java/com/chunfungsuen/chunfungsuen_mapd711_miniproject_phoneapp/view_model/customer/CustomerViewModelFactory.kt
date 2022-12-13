package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerRepository

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class CustomerViewModelFactory(val customerRepository: CustomerRepository)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(CustomerRepository::class.java)
            .newInstance(customerRepository)
    }
}