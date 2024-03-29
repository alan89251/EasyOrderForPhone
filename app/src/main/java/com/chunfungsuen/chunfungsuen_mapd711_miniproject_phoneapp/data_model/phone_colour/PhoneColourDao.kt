package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
@Dao
interface PhoneColourDao {
    @Query("SELECT * FROM phone_colour WHERE product_id = :productId")
    fun getPhoneColours(productId: Int): LiveData<List<PhoneColourModel>>?
}