package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.wish_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item.WishItemRepository

class WishListViewModelFactory(val wishItemRepository: WishItemRepository)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(WishItemRepository::class.java)
            .newInstance(wishItemRepository)
    }
}