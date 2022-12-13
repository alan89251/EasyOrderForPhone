package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity

import androidx.lifecycle.LiveData

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class StorageCapacityRepository(private val storageCapacityDao: StorageCapacityDao) {
    fun getStorageCapacities(productId: Int): LiveData<List<StorageCapacityModel>>? {
        return storageCapacityDao.getStorageCapacities(productId)
    }
}