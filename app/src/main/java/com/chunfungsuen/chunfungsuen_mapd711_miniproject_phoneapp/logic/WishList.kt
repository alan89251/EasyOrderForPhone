package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.logic

import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item.WishItemModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item.WishItemRepository

class WishList {
    private val wishListRepository: WishItemRepository
    private var wishListCache: MutableSet<WishItemModel>
    private var customerId: Int

    /**
     * Load the wish list from repository at init
     */
    constructor(wishListRepository: WishItemRepository, customerId: Int) {
        this.wishListRepository = wishListRepository
        this.customerId = customerId

        // load wish list from repository
        wishListCache = mutableSetOf()
        wishListRepository.getAllWishItems(this.customerId).forEach {
            wishListCache.add(it)
        }
    }

    fun getAllWishItems(): List<WishItemModel> {
        // return the copy of the cache
        return ArrayList(wishListCache)
    }

    fun addProduct(productId: Int) {
        wishListCache.add(WishItemModel(productId)) // save to cache
        // save change to repository
        wishListRepository.truncateExistAndSaveWishItemsForCustomer(customerId, wishListCache.toList())
    }

    fun removeProduct(productId: Int) {
        // remove from cache
        wishListCache.removeIf{
            it.ProductId == productId
        }
        // save change to repository
        wishListRepository.truncateExistAndSaveWishItemsForCustomer(customerId, wishListCache.toList())
    }

    fun isOnWishList(productId: Int): Boolean {
        wishListCache.forEach {
            if (it.ProductId == productId) {
                return true
            }
        }
        return false
    }
}