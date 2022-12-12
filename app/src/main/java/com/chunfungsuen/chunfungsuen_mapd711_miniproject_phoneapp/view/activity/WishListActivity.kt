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
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item.WishItemRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.database.PhoneOrderServiceDatabase
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.file_system.WishItemDaoFileSystem
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.logic.MenuOnSelectHandler
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter.WishListListViewAdapter
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.wish_list.WishListViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.wish_list.WishListViewModelFactory

class WishListActivity : AppCompatActivity() {
    private lateinit var menuOnSelectHandler: MenuOnSelectHandler
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var wishListViewModel: WishListViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var wishListListView: ListView
    private lateinit var wishListListViewAdapter: WishListListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_list)

        menuOnSelectHandler = MenuOnSelectHandler(R.id.menu_wish_list, this)

        // read from share preferences
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("phoneOrderServicePreferences", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "").toString()

        wishListListView = findViewById(R.id.wish_list)

        // init database
        val phoneOrderServiceDatabase = PhoneOrderServiceDatabase.getDatabaseClient(this@WishListActivity)
        // create view model for customer
        customerViewModel = ViewModelProvider(this,
            CustomerViewModelFactory(
                CustomerRepository(
                    phoneOrderServiceDatabase!!.customerDao()
                )
            )
        ).get(CustomerViewModel::class.java)

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

        // create view model for product
        productViewModel = ViewModelProvider(this,
            ProductViewModelFactory(
                ProductRepository(
                    phoneOrderServiceDatabase!!.productDao()
                )
            )
        ).get(ProductViewModel::class.java)

        // loading data from repository
        customerViewModel.loadCustomer(userName)
        customerViewModel.customer!!.observe(this) { loadWishList(it.CustId!!) }
    }

    /**
     * load wish list from repository
     * then load products which are on the list
     */
    private fun loadWishList(customerId: Int) {
        // load wish list from repository
        wishListViewModel.loadWishListByCustomer(customerId)

        // load products which are on the list
        val productIds = ArrayList<Int>()
        wishListViewModel.wishList!!.getAllWishItems().forEach {
            productIds.add(it.ProductId)
        }
        productViewModel.getProductsByIds(productIds)!!
            .observe(this, ::setWishListContent)
    }

    /**
     * Set the wish list content
     */
    private fun setWishListContent(products: List<ProductModel>) {
        wishListListViewAdapter = WishListListViewAdapter(
            { productId -> return@WishListListViewAdapter wishListViewModel.wishList!!.isOnWishList(productId) },
            { productId -> wishListViewModel.wishList!!.addProduct(productId) },
            { productId -> wishListViewModel.wishList!!.removeProduct(productId) },
            ::removeProductFromWishListListView,
            this,
            android.R.layout.simple_list_item_1,
            products
        )
        wishListListView.adapter = wishListListViewAdapter
    }

    /**
     * remove the product from the list view
     */
    private fun removeProductFromWishListListView(product: ProductModel) {
        wishListListViewAdapter.remove(product)
        wishListListViewAdapter.notifyDataSetChanged()
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
}