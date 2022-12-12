package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour.PhoneColourModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour.PhoneColourRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity.StorageCapacityModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity.StorageCapacityRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item.WishItemRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.database.PhoneOrderServiceDatabase
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.file_system.WishItemDaoFileSystem
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.logic.MenuOnSelectHandler
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter.ProductListViewAdapter
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.order.OrderViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.order.OrderViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_colour.PhoneColourViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_colour.PhoneColourViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_price.PhonePriceViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_price.PhonePriceViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.storage_capacity.StorageCapacityViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.storage_capacity.StorageCapacityViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.wish_list.WishListViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.wish_list.WishListViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class OrderActivity : AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productListView: ListView
    private lateinit var selectedProduct: ProductModel
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var storageCapacityViewModel: StorageCapacityViewModel
    private lateinit var phoneColourViewModel: PhoneColourViewModel
    private lateinit var phonePriceViewModel: PhonePriceViewModel
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
        /*productListView.setOnItemClickListener { parent, _, position, _ ->
            // When user click an item in the product list view,
            // save it as the selected product and change the content view to a short summary
            // and also ask for an order date
            selectedProduct = parent.getItemAtPosition(position) as ProductModel
            showOrderForm()
        }*/

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

        // create view model for storage capacity
        storageCapacityViewModel = ViewModelProvider(this,
            StorageCapacityViewModelFactory(
                StorageCapacityRepository(
                    phoneOrderServiceDatabase!!.storageCapacityDao()
                )
            )
        ).get(StorageCapacityViewModel::class.java)

        // create view model for phone colour
        phoneColourViewModel = ViewModelProvider(this,
            PhoneColourViewModelFactory(
                PhoneColourRepository(
                    phoneOrderServiceDatabase!!.phoneColourDao()
                )
            )
        ).get(PhoneColourViewModel::class.java)

        // create view model for phone price
        phonePriceViewModel = ViewModelProvider(this,
            PhonePriceViewModelFactory(
                PhonePriceRepository(
                    phoneOrderServiceDatabase!!.phonePriceDao()
                )
            )
        ).get(PhonePriceViewModel::class.java)

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
        val intent: Intent? = menuOnSelectHandler.createIntent(item)
        if (intent != null) {
            startActivity(intent)
        }

        return true
    }

    private fun updateProductList(productList: List<ProductModel>) {
        // Get the customer id from repository by user name
        customerViewModel.customer!!.observe(this, Observer {
            // Get the wish list from the repository
            wishListViewModel.loadWishListByCustomer(it.CustId!!)

            // set product list
            productListView.adapter = ProductListViewAdapter(
                { productId -> return@ProductListViewAdapter wishListViewModel.wishList!!.isOnWishList(productId) },
                { productId -> wishListViewModel.wishList!!.addProduct(productId) },
                { productId -> wishListViewModel.wishList!!.removeProduct(productId) },
                ::onProductSelected,
                this,
                android.R.layout.simple_list_item_1,
                productList)
        })
    }

    private fun onProductSelected(product: ProductModel) {
        selectedProduct = product
        showOrderForm()
    }

    /**
     * Set to another content view that display a short summary of the info of the phone selected by the user
     * Also, ask for order date, storage capacity, phone colour
     */
    private fun showOrderForm() {
        setContentView(R.layout.order_form)

        // get storage capacity list of the product from repository
        // and use it to set the radio group to let user choose a capacity
        storageCapacityViewModel.getStorageCapacities(selectedProduct.ProductId!!)!!
            .observe(this, ::setStorageCapacityRadioGroupBtns)

        // get phone colour list of the product from repository
        // and use it to set the spinner to let user choose a colour
        phoneColourViewModel.getPhoneColours(selectedProduct.ProductId!!)!!
            .observe(this, ::setColourSpinnerContent)

        findViewById<TextView>(R.id.order_form_phone_model).text = selectedProduct.PhoneModel
        findViewById<TextView>(R.id.order_form_product_id).text = selectedProduct.ProductId.toString()
        findViewById<TextView>(R.id.order_form_phone_make).text = selectedProduct.PhoneMake

        // config observer for UI
        val priceTextView = findViewById<TextView>(R.id.order_form_price)
        phonePriceViewModel.price.observe(this) {
            priceTextView.text = "$" + it.toString()
        }

        // config event handlers for UIs
        findViewById<RadioGroup>(R.id.storage_capacity_radio_group).setOnCheckedChangeListener(getPriceFromRepository)
        findViewById<Button>(R.id.move_to_shop_choosing_btn).setOnClickListener(onMoveToShopChoosingBtnClicked)
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

    /**
     * Set the storage capacity options in the radio group
     */
    private fun setStorageCapacityRadioGroupBtns(storageCapacities: List<StorageCapacityModel>) {
        val storageCapacityRadioGroup = findViewById<RadioGroup>(R.id.storage_capacity_radio_group)
        for (storageCapacity in storageCapacities) {
            storageCapacityRadioGroup.addView(createStorageCapacityRadioBtn(storageCapacity.StorageCapacity))
        }
    }

    /**
     * create radio button for storage capacity option
     */
    private fun createStorageCapacityRadioBtn(storageCapacity: String): RadioButton {
        val radioButton = RadioButton(this)
        radioButton.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        radioButton.setTextColor(resources.getColor(R.color.storageRadioBtnTextColor))
        radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, resources.getInteger(R.integer.storageRadioBtnTextSize).toFloat())
        radioButton.text = storageCapacity
        return radioButton
    }

    /**
     * add options of phone colour in the spinner
     * also, set the prompt text of the spinner
     */
    private fun setColourSpinnerContent(colours: List<PhoneColourModel>) {
        val spinnerContents = ArrayList<String>(colours.size)
        for (colour in colours) {
            spinnerContents.add(colour.Colour)
        }

        val colourSpinner = findViewById<Spinner>(R.id.color_spinner)
        colourSpinner.prompt = resources.getString(R.string.color_spinner_prompt)
        colourSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            spinnerContents
        )
    }

    // handle the onClick event of move_to_shop_choosing_btn
    private val onMoveToShopChoosingBtnClicked = View.OnClickListener {
        val orderDateStr = findViewById<EditText>(R.id.order_form_order_date).text.toString()
        // validation
        // use LocalDate to parse the string. If it throws exception, mean the date format is invalid
        try {
            val orderDate = LocalDate.parse(orderDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val currentDate = LocalDate.now() // get current date
            if (orderDate.isBefore(currentDate)) {
                Toast.makeText(this@OrderActivity, "Order date cannot earlier than current date", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
        }
        catch (e: DateTimeParseException) {
            Toast.makeText(this@OrderActivity, "Order date is not valid", Toast.LENGTH_SHORT)
                .show()
            return@OnClickListener
        }
        val storageCapacityRadioGroup = findViewById<RadioGroup>(R.id.storage_capacity_radio_group)
        // remind the customer to select a storage if he/she doesn't select one
        if (storageCapacityRadioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this@OrderActivity, "Please select a storage capacity", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }

        val selectedStorageCapacity = findViewById<RadioButton>(storageCapacityRadioGroup.checkedRadioButtonId).text.toString()
        val colourSpinnerSelectedView = findViewById<Spinner>(R.id.color_spinner).selectedView as TextView
        val selectedColour = colourSpinnerSelectedView.text.toString()

        // Get the customer id from repository by user name. Then, save order to repository
        customerViewModel.customer!!.observe(this, Observer {
            // fill the order
            val order = OrderModel(
                it.CustId!!,
                selectedProduct.ProductId!!,
                selectedColour,
                selectedStorageCapacity,
                orderDateStr,
                "",
                "",
                resources.getString(R.string.order_status_text_ordered)
            )

            // Navigate to map activity for selecting the phone store
            val intent = Intent(this@OrderActivity, MapsActivity::class.java)
            intent.putExtra("order", order)
            intent.putExtra("brand", selectedProduct.PhoneMake)
            startActivity(intent)
        })
    }

    // get the price of the phone with the selected storage capacity from the repository
    private val getPriceFromRepository = RadioGroup.OnCheckedChangeListener { radioGroup, i ->
        // Clear the observers of the live data of the previous query result
        phonePriceViewModel.priceQueryResult?.removeObservers(this)
        // get the price of the phone with the selected storage capacity from the repository
        phonePriceViewModel.priceQueryResult = phonePriceViewModel.getPhonePrice(
            selectedProduct.ProductId!!,
            (radioGroup.getChildAt(i - 1) as RadioButton).text.toString()
        )
        phonePriceViewModel.priceQueryResult?.observe(this) {
            phonePriceViewModel.price.value = it.Price
        }
    }
}