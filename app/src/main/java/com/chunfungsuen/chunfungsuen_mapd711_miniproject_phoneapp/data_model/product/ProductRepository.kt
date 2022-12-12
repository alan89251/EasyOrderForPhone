package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product

import androidx.lifecycle.LiveData

class ProductRepository(private val productDao: ProductDao)
{
    fun getAllProduct(): LiveData<List<ProductModel>>? {
        return productDao.getAllProduct()
    }

    fun getProductByPhoneMake(phoneMake: String): LiveData<List<ProductModel>>? {
        return productDao.getProductByPhoneMake(phoneMake)
    }

    fun getProductById(productId: Int): LiveData<ProductModel>? {
        return productDao.getProductById(productId)
    }

    fun getProductsByIds(productIds: List<Int>): LiveData<List<ProductModel>>? {
        return productDao.getProductsByIds(productIds)
    }
}