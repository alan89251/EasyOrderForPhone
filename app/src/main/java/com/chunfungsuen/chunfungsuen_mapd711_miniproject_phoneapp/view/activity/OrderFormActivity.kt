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
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour.PhoneColourModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour.PhoneColourRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity.StorageCapacityModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity.StorageCapacityRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.database.PhoneOrderServiceDatabase
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.logic.MenuOnSelectHandler
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_colour.PhoneColourViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_colour.PhoneColourViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_price.PhonePriceViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.phone_price.PhonePriceViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.product.ProductViewModelFactory
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.storage_capacity.StorageCapacityViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.storage_capacity.StorageCapacityViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class OrderFormActivity : AppCompatActivity() {
    private lateinit var menuOnSelectHandler: MenuOnSelectHandler
    private lateinit var selectedProduct: ProductModel
    private lateinit var storageCapacityViewModel: StorageCapacityViewModel
    private lateinit var phoneColourViewModel: PhoneColourViewModel
    private lateinit var phonePriceViewModel: PhonePriceViewModel
    private lateinit var productViewModel: ProductViewModel
    private var customerId: Int = 0

    /**
     * display a short summary of the info of the phone selected by the user
     * Also, ask for order date, storage capacity, phone colour
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_form)

        menuOnSelectHandler = MenuOnSelectHandler(null, this)

        // read from share preferences
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("phoneOrderServicePreferences", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "").toString()

        customerId = intent.getIntExtra("customerId", 0)
        val selectedProductId = intent.getIntExtra("selectedProductId", 0)

        // init database
        val phoneOrderServiceDatabase = PhoneOrderServiceDatabase.getDatabaseClient(this@OrderFormActivity)

        // create view model for product
        productViewModel = ViewModelProvider(this,
            ProductViewModelFactory(
                ProductRepository(
                    phoneOrderServiceDatabase!!.productDao()
                )
            )
        ).get(ProductViewModel::class.java)

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

        // loading selected product from repository
        productViewModel.getProductById(selectedProductId)!!
            .observe(this, ::onLoadedProduct)
    }

    /**
     * Set the UI after loaded product from the repository
     */
    private fun onLoadedProduct(product: ProductModel) {
        selectedProduct = product

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

    // handle the onClick event of move_to_shop_choosing_btn
    private val onMoveToShopChoosingBtnClicked = View.OnClickListener {
        val orderDateStr = findViewById<EditText>(R.id.order_form_order_date).text.toString()
        // validation
        // use LocalDate to parse the string. If it throws exception, mean the date format is invalid
        try {
            val orderDate = LocalDate.parse(orderDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val currentDate = LocalDate.now() // get current date
            if (orderDate.isBefore(currentDate)) {
                Toast.makeText(this@OrderFormActivity, "Order date cannot earlier than current date", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
        }
        catch (e: DateTimeParseException) {
            Toast.makeText(this@OrderFormActivity, "Order date is not valid", Toast.LENGTH_SHORT)
                .show()
            return@OnClickListener
        }
        val storageCapacityRadioGroup = findViewById<RadioGroup>(R.id.storage_capacity_radio_group)
        // remind the customer to select a storage if he/she doesn't select one
        if (storageCapacityRadioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this@OrderFormActivity, "Please select a storage capacity", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }

        val selectedStorageCapacity = findViewById<RadioButton>(storageCapacityRadioGroup.checkedRadioButtonId).text.toString()
        val colourSpinnerSelectedView = findViewById<Spinner>(R.id.color_spinner).selectedView as TextView
        val selectedColour = colourSpinnerSelectedView.text.toString()

        // fill the order
        val order = OrderModel(
            customerId,
            selectedProduct.ProductId!!,
            selectedColour,
            selectedStorageCapacity,
            orderDateStr,
            "",
            "",
            resources.getString(R.string.order_status_text_ordered)
        )

        // Navigate to map activity for selecting the phone store
        val intent = Intent(this@OrderFormActivity, MapsActivity::class.java)
        intent.putExtra("order", order)
        intent.putExtra("brand", selectedProduct.PhoneMake)
        startActivity(intent)
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