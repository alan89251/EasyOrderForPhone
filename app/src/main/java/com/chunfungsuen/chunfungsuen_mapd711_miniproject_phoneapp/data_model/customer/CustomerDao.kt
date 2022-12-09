package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomer(customerModel: CustomerModel)

    @Query("SELECT * FROM customer WHERE UserName = :userName")
    fun getCustomer(userName: String?): LiveData<CustomerModel>

    @Update
    fun update(customerModel: CustomerModel)
}