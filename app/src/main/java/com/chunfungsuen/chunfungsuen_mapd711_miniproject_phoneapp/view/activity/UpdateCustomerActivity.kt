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
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.database.PhoneOrderServiceDatabase
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.logic.MenuOnSelectHandler
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModelFactory

class UpdateCustomerActivity : AppCompatActivity() {
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var userName: String
    private lateinit var menuOnSelectHandler: MenuOnSelectHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_customer)

        menuOnSelectHandler = MenuOnSelectHandler(R.id.menu_update_customer_info, this)

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
            findViewById<EditText>(R.id.phone).setText(it.Telephone)
            findViewById<EditText>(R.id.credit_card_no).setText(it.CreditCardNo)
            findViewById<EditText>(R.id.card_type).setText(it.CardType)
            findViewById<EditText>(R.id.card_expiration).setText(it.CardExpiration)
            findViewById<EditText>(R.id.credit_card_CVC).setText(it.CreditCardCVC)

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
        val intent: Intent? = menuOnSelectHandler.createIntent(item)
        if (intent != null) {
            startActivity(intent)
        }

        return true
    }

    fun updateCustomerInfo(v: View) {
        val firstname = findViewById<EditText>(R.id.firstname).text.toString()
        val lastname = findViewById<EditText>(R.id.lastname).text.toString()
        val address = findViewById<EditText>(R.id.address).text.toString()
        val city = findViewById<EditText>(R.id.city).text.toString()
        val country = findViewById<EditText>(R.id.country).text.toString()
        val postalCode = findViewById<EditText>(R.id.postal_code).text.toString()
        val telephone = findViewById<EditText>(R.id.phone).text.toString()
        val creditCardNo = findViewById<EditText>(R.id.credit_card_no).text.toString()
        val cardType = findViewById<EditText>(R.id.card_type).text.toString()
        val cardExpiration = findViewById<EditText>(R.id.card_expiration).text.toString()
        val creditCardCVC = findViewById<EditText>(R.id.credit_card_CVC).text.toString()

        // validations
        if (!CustomerModel.isValidFirstname(firstname)) {
            Toast.makeText(this@UpdateCustomerActivity, "firstname is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!CustomerModel.isValidLastname(lastname)) {
            Toast.makeText(this@UpdateCustomerActivity, "lastname is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!CustomerModel.isValidAddress(address)) {
            Toast.makeText(this@UpdateCustomerActivity, "address is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!CustomerModel.isValidCity(city)) {
            Toast.makeText(this@UpdateCustomerActivity, "city is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!CustomerModel.isValidCountry(country)) {
            Toast.makeText(this@UpdateCustomerActivity, "country is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!CustomerModel.isValidPostalCode(postalCode)) {
            Toast.makeText(this@UpdateCustomerActivity, "postal code is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!CustomerModel.isValidTelephone(telephone)) {
            Toast.makeText(this@UpdateCustomerActivity, "telephone is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!CustomerModel.isValidCreditCardNo(creditCardNo)) {
            Toast.makeText(this@UpdateCustomerActivity, "credit card no is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        // Credit card type should contain alphabet and whitespace only
        if (!CustomerModel.isValidCardType(cardType)) {
            Toast.makeText(this@UpdateCustomerActivity, "card type is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        // Credit card expiration date should either in yyyy/MM/dd, yyyy/M/dd, yyyy/MM/d or yyyy/M/d
        if (!CustomerModel.isValidCardExpiration(cardExpiration)) {
            Toast.makeText(this@UpdateCustomerActivity, "card expiration is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        // Credit card CVV/CVC must be 3 digits
        if (!CustomerModel.isValidCreditCardCVC(creditCardCVC)) {
            Toast.makeText(this@UpdateCustomerActivity, "credit card CVC is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // update the customer info to repository
        val updatedCustomer = customerViewModel.customer!!.value!! // get the current loaded customer info
        val newPassword = findViewById<EditText>(R.id.new_password).text.toString()
        if (newPassword.isNotEmpty()) {
            updatedCustomer.Password = findViewById<EditText>(R.id.new_password).text.toString()
        }
        updatedCustomer.Firstname = firstname
        updatedCustomer.Lastname = lastname
        updatedCustomer.Address = address
        updatedCustomer.City = city
        updatedCustomer.Country = country
        updatedCustomer.PostalCode = postalCode
        updatedCustomer.Telephone = telephone
        updatedCustomer.CreditCardNo = creditCardNo
        updatedCustomer.CardType = cardType
        updatedCustomer.CardExpiration = cardExpiration
        updatedCustomer.CreditCardCVC = creditCardCVC
        // save the update to repository
        customerViewModel.updateCustomer(updatedCustomer, {
            Toast.makeText(this@UpdateCustomerActivity, "Customer information updated!", Toast.LENGTH_SHORT)
                .show()
        })
    }
}