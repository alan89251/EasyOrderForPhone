package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item

interface WishItemDao {
    fun getAllWishItems(customerId: Int): List<WishItemModel>

    /**
     * Truncate all existing wish items for customer
     * Then save the new wish items
     */
    fun TruncateExistAndSaveWishItemsForCustomer(customerId: Int, wishItems: List<WishItemModel>)
}