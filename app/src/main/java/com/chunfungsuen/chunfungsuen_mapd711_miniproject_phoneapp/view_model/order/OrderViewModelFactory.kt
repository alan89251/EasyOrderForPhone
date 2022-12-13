package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderRepository

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class OrderViewModelFactory(val orderRepository: OrderRepository)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(OrderRepository::class.java)
            .newInstance(orderRepository)
    }
}