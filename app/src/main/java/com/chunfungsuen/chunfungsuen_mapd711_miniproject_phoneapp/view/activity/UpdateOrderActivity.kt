package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.database.PhoneOrderServiceDatabase
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter.OrderListViewAdapter
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.order.OrderViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.order.OrderViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_price.PhonePriceViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_price.PhonePriceViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModelFactory

class UpdateOrderActivity : AppCompatActivity() {
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var orderListView: ListView
    private lateinit var phonePriceViewModel: PhonePriceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_order)

        // read from share preferences
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("phoneOrderServicePreferences", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "").toString()

        // config UIs
        orderListView = findViewById(R.id.order_list)

        // init database
        val phoneOrderServiceDatabase = PhoneOrderServiceDatabase.getDatabaseClient(this@UpdateOrderActivity)

        // create view model for customer
        customerViewModel = ViewModelProvider(this,
            CustomerViewModelFactory(
                CustomerRepository(
                    phoneOrderServiceDatabase!!.customerDao()
                )
            )
        ).get(CustomerViewModel::class.java)

        // create view model for product
        productViewModel = ViewModelProvider(this,
            ProductViewModelFactory(
                ProductRepository(
                    phoneOrderServiceDatabase!!.productDao()
                )
            )
        ).get(ProductViewModel::class.java)

        // create view model for order
        orderViewModel = ViewModelProvider(this,
            OrderViewModelFactory(
                OrderRepository(
                    phoneOrderServiceDatabase!!.orderDao()
                )
            )
        ).get(OrderViewModel::class.java)

        // create view model for phone price
        phonePriceViewModel = ViewModelProvider(this,
            PhonePriceViewModelFactory(
                PhonePriceRepository(
                    phoneOrderServiceDatabase!!.phonePriceDao()
                )
            )
        ).get(PhonePriceViewModel::class.java)

        // init order list in the view model
        orderViewModel.initOrderList()
        orderViewModel.orderList!!.observe(this, ::updateOrderList)
        // load customer info from the repository
        customerViewModel.loadCustomer(userName)
        customerViewModel.customer!!.observe(this, ::loadOrderListForCustomer)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.phone_order_service_menu, menu)
        return true
    }

    /**
     * Navigate to the activity of the selected menu item
     * Do nothing if the user select the current activity
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_phone_model_list -> {
                val intent = Intent(this@UpdateOrderActivity, OrderActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_update_customer_info -> {
                val intent = Intent(this@UpdateOrderActivity, UpdateCustomerActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

    // Load order list from the repository for the logined user
    // This function is used as Observer for the customer view model
    private fun loadOrderListForCustomer(customer: CustomerModel) {
        // set orderViewModel.customerId will trigger the reload of the orderList
        orderViewModel.customerId!!.value = customer.CustId
    }

    private fun updateOrderList(orderList: List<OrderModel>) {
        if (orderList.isEmpty()) {
            return
        }

        orderListView.adapter = OrderListViewAdapter(
            customerViewModel.customer!!.value!!,
            ::getProductByIdAsync,
            ::getPriceAsync,
            ::updateOrder,
            ::onCheckStoreBtnClicked,
            this,
            android.R.layout.simple_list_item_1,
            orderList
        )
    }

    /**
     * Get product info from repository async-ly
     * After received the result, call the callback: "onResult"
     */
    private fun getProductByIdAsync(productId: Int, onResult: (ProductModel) -> Unit) {
        productViewModel.getProductById(productId)!!.observe(this, onResult)
    }

    /**
     * get phone price from repository async-ly
     * After received the result, call the callback: "onResult"
     */
    private fun getPriceAsync(productId: Int, storageCapacity: String, onResult: (PhonePriceModel) -> Unit) {
        phonePriceViewModel.getPhonePrice(productId, storageCapacity)!!.observe(this, onResult)
    }

    /**
     * Update the order that saved in the repository
     */
    private fun updateOrder(order: OrderModel) {
        orderViewModel.updateOrder(order)
    }

    /**
     * Navigate to Check Store Activity to check the location and the route to the phone store
     * of this order
     */
    private fun onCheckStoreBtnClicked(storePlaceId: String) {
        val intent = Intent(this@UpdateOrderActivity, CheckStoreActivity::class.java)
        startActivity(intent)
    }
}