package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item

class WishItemRepository(
    private val wishItemDao: WishItemDao
) {
    fun getAllWishItems(customerId: Int): List<WishItemModel> {
        return wishItemDao.getAllWishItems(customerId)
    }

    /**
     * Truncate all existing wish items for customer
     * Then save the new wish items
     */
    fun truncateExistAndSaveWishItemsForCustomer(customerId: Int, wishItems: List<WishItemModel>) {
        wishItemDao.truncateExistAndSaveWishItemsForCustomer(customerId, wishItems)
    }
}