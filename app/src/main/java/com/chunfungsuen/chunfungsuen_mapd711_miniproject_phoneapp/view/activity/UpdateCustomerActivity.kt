package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.database.PhoneOrderServiceDatabase
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModelFactory

class UpdateCustomerActivity : AppCompatActivity() {
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_customer)

        // read from share preferences
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("phoneOrderServicePreferences", MODE_PRIVATE)
        userName = sharedPreferences.getString("userName", "").toString()

        // disable the update customer button before the customer info is returned from ths repository
        val updateCustomerBtn = findViewById<Button>(R.id.update_customer_info_btn)
        updateCustomerBtn.isEnabled = false

        // init database
        val phoneOrderServiceDatabase = PhoneOrderServiceDatabase.getDatabaseClient(this@UpdateCustomerActivity)

        // create view model for customer
        customerViewModel = ViewModelProvider(this,
            CustomerViewModelFactory(
                CustomerRepository(
                    phoneOrderServiceDatabase!!.customerDao()
                )
            )
        ).get(CustomerViewModel::class.java)

        // load customer info from the repository
        customerViewModel.loadCustomer(userName)
        customerViewModel.customer!!.observe(this, Observer {
            // set the value of the input controls by the customer info
            findViewById<EditText>(R.id.firstname).setText(it.Firstname)
            findViewById<EditText>(R.id.lastname).setText(it.Lastname)
            findViewById<EditText>(R.id.address).setText(it.Address)
            findViewById<EditText>(R.id.city).setText(it.City)
            findViewById<EditText>(R.id.country).setText(it.Country)
            findViewById<EditText>(R.id.postal_code).setText(it.PostalCode)
            updateCustomerBtn.isEnabled = true
        })
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
                val intent = Intent(this@UpdateCustomerActivity, OrderActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_update_order -> {
                val intent = Intent(this@UpdateCustomerActivity, UpdateOrderActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }

    fun updateCustomerInfo(v: View) {
        val updatedCustomer = customerViewModel.customer!!.value!! // get the current loaded customer info
        // update the customer info by the values from UIs
        val newPassword = findViewById<EditText>(R.id.new_password).text.toString()
        if (newPassword.isNotEmpty()) {
            updatedCustomer.Password = findViewById<EditText>(R.id.new_password).text.toString()
        }
        updatedCustomer.Firstname = findViewById<EditText>(R.id.firstname).text.toString()
        updatedCustomer.Lastname = findViewById<EditText>(R.id.lastname).text.toString()
        updatedCustomer.Address = findViewById<EditText>(R.id.address).text.toString()
        updatedCustomer.City = findViewById<EditText>(R.id.city).text.toString()
        updatedCustomer.Country = findViewById<EditText>(R.id.country).text.toString()
        updatedCustomer.PostalCode = findViewById<EditText>(R.id.postal_code).text.toString()
        updatedCustomer.Telephone = findViewById<EditText>(R.id.phone).text.toString()
        updatedCustomer.CreditCardNo = findViewById<EditText>(R.id.credit_card_no).text.toString()
        updatedCustomer.CardType = findViewById<EditText>(R.id.card_type).text.toString()
        updatedCustomer.CardExpiration = findViewById<EditText>(R.id.card_expiration).text.toString()
        updatedCustomer.CreditCardCVC = findViewById<EditText>(R.id.credit_card_CVC).text.toString()
        // save the update to repository
        customerViewModel.updateCustomer(updatedCustomer, {
            Toast.makeText(this@UpdateCustomerActivity, "Customer information updated!", Toast.LENGTH_SHORT)
                .show()
        })
    }
}