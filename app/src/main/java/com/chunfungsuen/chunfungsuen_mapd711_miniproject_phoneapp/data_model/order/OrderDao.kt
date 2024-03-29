package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(orderModel: OrderModel)

    @Query("SELECT * FROM `order` WHERE CustId = :custId")
    fun getOrderByCustId(custId: Int): LiveData<List<OrderModel>>

    @Update
    fun update(orderModel: OrderModel)
}