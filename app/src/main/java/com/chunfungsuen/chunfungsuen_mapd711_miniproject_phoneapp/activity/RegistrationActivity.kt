package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerRepository
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.database.PhoneOrderServiceDatabase
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer.CustomerViewModelFactory

class RegistrationActivity : AppCompatActivity() {
    private lateinit var customerViewModel: CustomerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // init database
        val phoneOrderServiceDatabase = PhoneOrderServiceDatabase.getDatabaseClient(this@RegistrationActivity)
        // get customer view model
        customerViewModel = ViewModelProvider(this,
            CustomerViewModelFactory(
                CustomerRepository(
                    phoneOrderServiceDatabase!!.customerDao()
                )
            )
        ).get(CustomerViewModel::class.java)
    }

    /**
     * save the customer info to the repository
     */
    fun submitSignUp(v: View) {
        val newUserName = findViewById<EditText>(R.id.new_user_name).text.toString()
        val newPassword = findViewById<EditText>(R.id.new_password).text.toString()
        val firstname = findViewById<EditText>(R.id.firstname).text.toString()
        val lastname = findViewById<EditText>(R.id.lastname).text.toString()
        val address = findViewById<EditText>(R.id.address).text.toString()
        val city = findViewById<EditText>(R.id.city).text.toString()
        val country = findViewById<EditText>(R.id.country).text.toString()
        val postalCode = findViewById<EditText>(R.id.postal_code).text.toString()

        // validations
        if (newUserName.isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "user name is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (newPassword.isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "password is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (firstname.isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "firstname is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (lastname.isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "lastname is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (address.isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "address is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (city.isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "city is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (country.isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "country is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (postalCode.isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "postalCode is not valid", Toast.LENGTH_SHORT)
                .show()
            return
        }

        var customerModel = CustomerModel(
            newUserName,
            newPassword,
            firstname,
            lastname,
            address,
            city,
            country,
            postalCode
        )

        customerViewModel.insertCustomer(customerModel)

        // save user name to the share preference
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("phoneOrderServicePreferences", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("userName", customerModel.UserName)
        editor.commit()

        // Navigate to order activity
        val intent = Intent(this@RegistrationActivity, OrderActivity::class.java)
        startActivity(intent)
    }
}