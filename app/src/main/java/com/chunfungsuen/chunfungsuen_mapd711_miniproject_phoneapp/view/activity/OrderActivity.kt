package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.database.PhoneOrderServiceDatabase
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.order.OrderViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.order.OrderViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class OrderActivity : AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productListView: ListView
    private lateinit var selectedProduct: ProductModel
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        // read from share preferences
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("phoneOrderServicePreferences", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "").toString()

        // config UIs
        findViewById<TextView>(R.id.welcome_message_place_order).text = String.format(
            resources.getString(R.string.welcome_message_place_order_template),
            userName
        )
        productListView = findViewById(R.id.phone_model_list)
        productListView.setOnItemClickListener { parent, _, position, _ ->
            // When user click an item in the product list view,
            // save it as the selected product and change the content view to a short summary
            // and also ask for an order date
            selectedProduct = parent.getItemAtPosition(position) as ProductModel
            showOrderForm()
        }

        // init database
        val phoneOrderServiceDatabase = PhoneOrderServiceDatabase.getDatabaseClient(this@OrderActivity)

        // create view model for product
        productViewModel = ViewModelProvider(this,
            ProductViewModelFactory(
                ProductRepository(
                    phoneOrderServiceDatabase!!.productDao()
                )
            )
        ).get(ProductViewModel::class.java)

        // create view model for customer
        customerViewModel = ViewModelProvider(this,
            CustomerViewModelFactory(
                CustomerRepository(
                    phoneOrderServiceDatabase!!.customerDao()
                )
            )
        ).get(CustomerViewModel::class.java)

        // create view model for order
        orderViewModel = ViewModelProvider(this,
            OrderViewModelFactory(
                OrderRepository(
                    phoneOrderServiceDatabase!!.orderDao()
                )
            )
        ).get(OrderViewModel::class.java)

        // loading data from repository
        productViewModel.initProductList()
        productViewModel.productList!!.observe(this, ::updateProductList)
        customerViewModel.loadCustomer(userName)
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
            R.id.menu_update_order -> {
                val intent = Intent(this@OrderActivity, UpdateOrderActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_update_customer_info -> {
                val intent = Intent(this@OrderActivity, UpdateCustomerActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

    private fun updateProductList(productList: List<ProductModel>) {
        productListView.adapter = ProductListViewAdapter(this, android.R.layout.simple_list_item_1, productList)
    }

    /**
     * Set to another content view that display a short summary of the info of the phone selected by the user
     * Also, ask for an order date
     */
    private fun showOrderForm() {
        setContentView(R.layout.order_form)
        findViewById<TextView>(R.id.order_form_phone_model).text = selectedProduct.PhoneModel
        findViewById<TextView>(R.id.order_form_product_id).text = selectedProduct.ProductId.toString()
        findViewById<TextView>(R.id.order_form_phone_make).text = selectedProduct.PhoneMake
        findViewById<TextView>(R.id.order_form_phone_color).text = selectedProduct.PhoneColor
        findViewById<TextView>(R.id.order_form_storage_capacity).text = selectedProduct.StorageCapacity
        findViewById<TextView>(R.id.order_form_price).text = "$" + selectedProduct.Price.toString()

        findViewById<Button>(R.id.submit_order_btn).setOnClickListener {
            val orderDateStr = findViewById<EditText>(R.id.order_form_order_date).text.toString()
            // validation
            // use LocalDate to parse the string. If it throws exception, mean the date format is invalid
            try {
                val orderDate = LocalDate.parse(orderDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                val currentDate = LocalDate.now() // get current date
                if (orderDate.isBefore(currentDate)) {
                    Toast.makeText(this@OrderActivity, "Order date cannot earlier than current date", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            }
            catch (e: DateTimeParseException) {
                Toast.makeText(this@OrderActivity, "Order date is not valid", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // First, get the customer id from repository by user name. Then, save order to repository
            customerViewModel.customer!!.observe(this, Observer {
                // save order to repository
                val order = OrderModel(
                    it.CustId!!,
                    selectedProduct.ProductId!!,
                    orderDateStr,
                    resources.getString(R.string.order_status_text_ordered)
                )
                orderViewModel.insertOrder(order)

                // Navigate to update order activity
                val intent = Intent(this@OrderActivity, UpdateOrderActivity::class.java)
                startActivity(intent)
            })
        }
    }

    /**
     * When the filter btn is clicked, set the phone brand it represents for as selected
     * Then update the product list to show the product of that brand only
     */
    fun onFilterBtnClicked(v: View) {
        // update the value of the live data of brand as the selected brand
        // this would trigger the product list live data to reload from the repository
        // and also the product list view to update
        productViewModel.brand!!.value = when (v.id) {
            R.id.filter_apple_btn -> resources.getString(R.string.apple_brand_name)
            R.id.filter_samsung_btn -> resources.getString(R.string.samsung_brand_name)
            R.id.filter_google_pixel_btn -> resources.getString(R.string.google_pixel_brand_name)
            R.id.filter_oppo_btn -> resources.getString(R.string.oppo_brand_name)
            else -> "" // never hit this case
        }
    }
}