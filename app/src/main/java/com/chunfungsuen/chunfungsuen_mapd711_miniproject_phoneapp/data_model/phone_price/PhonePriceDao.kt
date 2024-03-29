package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
@Dao
interface PhonePriceDao {
    @Query("SELECT * FROM phone_price WHERE product_id = :productId AND storage_capacity = :storageCapacity")
    fun getPhonePrice(productId: Int, storageCapacity: String): LiveData<PhonePriceModel>?
}