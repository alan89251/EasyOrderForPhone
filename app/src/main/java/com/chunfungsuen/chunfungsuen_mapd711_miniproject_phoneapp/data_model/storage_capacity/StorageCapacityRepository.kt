package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity

import androidx.lifecycle.LiveData

class StorageCapacityRepository(private val storageCapacityDao: StorageCapacityDao) {
    fun getStorageCapacities(productId: Int): LiveData<List<StorageCapacityModel>>? {
        return storageCapacityDao.getStorageCapacities(productId)
    }
}