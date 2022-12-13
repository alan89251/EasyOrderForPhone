package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer

import androidx.lifecycle.LiveData

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
// I follow the instruction of this page to implement this class
// https://developer.android.com/codelabs/android-room-with-a-view-kotlin#8
class CustomerRepository(private val customerDao: CustomerDao)
{

    fun insert(customerModel: CustomerModel) {
        customerDao.insertCustomer(customerModel)
    }

    fun getCustomer(userName: String): LiveData<CustomerModel>? {
        return customerDao.getCustomer(userName)
    }

    fun updateCustomer(customer: CustomerModel) {
        customerDao.update(customer)
    }
}