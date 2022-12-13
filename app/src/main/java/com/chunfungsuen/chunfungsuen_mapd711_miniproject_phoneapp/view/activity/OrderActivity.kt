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
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item.WishItemRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.database.PhoneOrderServiceDatabase
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.file_system.WishItemDaoFileSystem
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.logic.MenuOnSelectHandler
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter.ProductListViewAdapter
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.order.OrderViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.order.OrderViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.wish_list.WishListViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.wish_list.WishListViewModelFactory

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class OrderActivity : AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productListView: ListView
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var menuOnSelectHandler: MenuOnSelectHandler
    private lateinit var wishListViewModel: WishListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        menuOnSelectHandler = MenuOnSelectHandler(R.id.menu_phone_model_list, this)

        // read from share preferences
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("phoneOrderServicePreferences", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "").toString()

        // config UIs
        findViewById<TextView>(R.id.welcome_message_place_order).text = String.format(
            resources.getString(R.string.welcome_message_place_order_template),
            userName
        )
        productListView = findViewById(R.id.phone_model_list)

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

        // create view model for wish list
        wishListViewModel = ViewModelProvider(this,
            WishListViewModelFactory(
                WishItemRepository(
                    WishItemDaoFileSystem(
                        this,
                        resources.getString(R.string.wish_list_storage_dir)
                    )
                )
            )
        ).get(WishListViewModel::class.java)

        // loading customer from repository
        customerViewModel.loadCustomer(userName)
        customerViewModel.customer!!.observe(this, ::onLoadedCustomer)
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
        val intent: Intent? = menuOnSelectHandler.createIntent(item)
        if (intent != null) {
            startActivity(intent)
        }

        return true
    }

    private fun onLoadedCustomer(customer: CustomerModel) {
        // loading product list from repository
        productViewModel.initProductList()
        productViewModel.productList!!.observe(this, ::updateProductList)
    }

    private fun updateProductList(productList: List<ProductModel>) {
        // Get the wish list from the repository
        wishListViewModel.loadWishListByCustomer(customerViewModel.customer!!.value!!.CustId!!)

        // set product list
        productListView.adapter = ProductListViewAdapter(
            { productId -> return@ProductListViewAdapter wishListViewModel.wishList!!.isOnWishList(productId) },
            { productId -> wishListViewModel.wishList!!.addProduct(productId) },
            { productId -> wishListViewModel.wishList!!.removeProduct(productId) },
            ::onProductSelected,
            this,
            android.R.layout.simple_list_item_1,
            productList)
    }

    private fun onProductSelected(product: ProductModel) {
        val intent = Intent(this@OrderActivity, OrderFormActivity::class.java)
        intent.putExtra("customerId", customerViewModel.customer!!.value!!.CustId)
        intent.putExtra("selectedProductId", product.ProductId)
        startActivity(intent)
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