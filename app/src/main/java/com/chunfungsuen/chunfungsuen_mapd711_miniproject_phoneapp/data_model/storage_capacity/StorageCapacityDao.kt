package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
@Dao
interface StorageCapacityDao {
    @Query("SELECT * FROM storage_capacity WHERE product_id = :productId")
    fun getStorageCapacities(productId: Int): LiveData<List<StorageCapacityModel>>?
}