package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.storage_capacity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity.StorageCapacityModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity.StorageCapacityRepository

class StorageCapacityViewModel(
    // dependency injection of StorageCapacityRepository
    private val storageCapacityRepository: StorageCapacityRepository
) : ViewModel()
{
    fun getStorageCapacities(productId: Int): LiveData<List<StorageCapacityModel>>? {
        return storageCapacityRepository.getStorageCapacities(productId)
    }
}