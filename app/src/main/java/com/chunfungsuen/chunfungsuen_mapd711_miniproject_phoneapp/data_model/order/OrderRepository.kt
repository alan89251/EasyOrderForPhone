package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order

import androidx.lifecycle.LiveData

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class OrderRepository(private val orderDao: OrderDao)
{
    fun insert(orderModel: OrderModel) {
        orderDao.insertOrder(orderModel)
    }

    fun getOrderByCustId(custId: Int): LiveData<List<OrderModel>> {
        return orderDao.getOrderByCustId(custId)
    }

    fun updateOrder(order: OrderModel) {
        orderDao.update(order)
    }
}