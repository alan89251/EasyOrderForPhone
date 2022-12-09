package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderViewModel(
    // dependency injection of orderRepository
    private val orderRepository: OrderRepository
)
    : ViewModel()
{
    var customerId: MutableLiveData<Int> = MutableLiveData(0)
        private set
    var orderList: LiveData<List<OrderModel>>? = null
        private set

    fun insertOrder(order: OrderModel) {
        CoroutineScope(Dispatchers.IO).launch {
            orderRepository.insert(order)
        }
    }

    // init the order list
    fun initOrderList() {
        orderList = Transformations.switchMap(customerId, {
            // return empty list if customer id has not been set
            if (it == 0) {
                return@switchMap MutableLiveData(ArrayList<OrderModel>())
            }

            return@switchMap orderRepository.getOrderByCustId(it)
        })
    }

    fun updateOrder(order: OrderModel) {
        CoroutineScope(Dispatchers.IO).launch {
            orderRepository.updateOrder(order)
        }
    }
}