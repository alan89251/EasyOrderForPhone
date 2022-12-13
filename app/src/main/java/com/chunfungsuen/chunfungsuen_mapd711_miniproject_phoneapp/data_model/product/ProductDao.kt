package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getAllProduct(): LiveData<List<ProductModel>>

    @Query("SELECT * FROM product WHERE PhoneMake = :phoneMake")
    fun getProductByPhoneMake(phoneMake: String?): LiveData<List<ProductModel>>?

    @Query("SELECT * FROM product WHERE ProductId = :productId")
    fun getProductById(productId: Int): LiveData<ProductModel>?

    @Query("SELECT * FROM product WHERE ProductId IN (:productIds)")
    fun getProductsByIds(productIds: List<Int>): LiveData<List<ProductModel>>?

}