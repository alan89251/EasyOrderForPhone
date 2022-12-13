package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductRepository

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class ProductViewModelFactory(val productRepository: ProductRepository)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ProductRepository::class.java)
            .newInstance(productRepository)
    }
}