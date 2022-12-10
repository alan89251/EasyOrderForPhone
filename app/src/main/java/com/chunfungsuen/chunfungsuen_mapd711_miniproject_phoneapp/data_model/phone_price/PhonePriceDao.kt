package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface PhonePriceDao {
    @Query("SELECT * FROM phone_price WHERE product_id = :productId AND storage_capacity_id = :storageCapacityId")
    fun getPhonePrice(productId: Int, storageCapacityId: Int): LiveData<PhonePriceModel>?
}