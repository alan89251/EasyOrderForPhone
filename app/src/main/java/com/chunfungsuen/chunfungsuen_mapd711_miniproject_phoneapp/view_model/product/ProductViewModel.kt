package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductRepository

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class ProductViewModel(
    // dependency injection of productRepository
    private val productRepository: ProductRepository
)
    : ViewModel()
{
    // used to filter the items shown on the product list
    var brand: MutableLiveData<String> = MutableLiveData("")

    var productList: LiveData<List<ProductModel>>? = null
        private set

    // init the live data of the product list
    // when brand is empty, load all the product.
    // otherwise, load the products of that brand only
    fun initProductList() {
        productList = Transformations.switchMap(brand, {
            // return all products when brand name is empty
            if (it == "") {
                return@switchMap productRepository.getAllProduct()
            }
            return@switchMap productRepository.getProductByPhoneMake(it)
        })
    }

    fun getProductById(productId: Int): LiveData<ProductModel>? {
        return productRepository.getProductById(productId)
    }

    fun getProductsByIds(productIds: List<Int>): LiveData<List<ProductModel>>? {
        return productRepository.getProductsByIds(productIds)
    }
}