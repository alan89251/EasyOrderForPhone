package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.wish_list

import androidx.lifecycle.ViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item.WishItemRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.logic.WishList

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class WishListViewModel(
    // dependency injection of WishItemRepository
    private val wishItemRepository: WishItemRepository
) : ViewModel() {
    var wishList: WishList? = null
        private set

    fun loadWishListByCustomer(customerId: Int) {
        wishList = WishList(wishItemRepository, customerId)
    }
}