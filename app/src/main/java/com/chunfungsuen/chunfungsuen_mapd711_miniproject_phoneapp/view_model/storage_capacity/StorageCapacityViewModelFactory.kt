package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.storage_capacity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity.StorageCapacityRepository

class StorageCapacityViewModelFactory(val storageCapacityRepository: StorageCapacityRepository)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(StorageCapacityRepository::class.java)
            .newInstance(storageCapacityRepository)
    }
}