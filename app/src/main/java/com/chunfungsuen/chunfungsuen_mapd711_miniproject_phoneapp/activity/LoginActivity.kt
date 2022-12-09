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

class LoginActivity : AppCompatActivity() {
    private lateinit var customerViewModel: CustomerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // init database
        val phoneOrderServiceDatabase = PhoneOrderServiceDatabase.getDatabaseClient(this@LoginActivity)

        // create view model for customer
        customerViewModel = ViewModelProvider(this,
            CustomerViewModelFactory(
                CustomerRepository(
                    phoneOrderServiceDatabase!!.customerDao()
                )
            )
        ).get(CustomerViewModel::class.java)
    }

    fun onSignInBtnClicked(v: View) {
        val userNameEditText = findViewById<EditText>(R.id.user_editText)
        customerViewModel.loadCustomer(userNameEditText.text.toString()) // load customer by user name
        customerViewModel.customer!!.observe(this, Observer {
            if (it == null) { // when user name is not exist
                Toast.makeText(this@LoginActivity, "User name or password is not valid", Toast.LENGTH_SHORT)
                    .show()
            }
            else if (it.Password != findViewById<EditText>(R.id.password_editText).text.toString()) {
                // when password is not correct
                Toast.makeText(this@LoginActivity, "User name or password is not valid", Toast.LENGTH_SHORT)
                    .show()
            }
            else { // login success
                // save user name to the share preference
                val sharedPreferences: SharedPreferences = this.getSharedPreferences("phoneOrderServicePreferences", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("userName", it.UserName)
                editor.commit()

                // Navigate to the order activity
                var intent = Intent(this@LoginActivity, OrderActivity::class.java)
                startActivity(intent)
            }
        })
    }

    fun onSignUpBtnClicked(v: View) {
        var intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
        startActivity(intent)
    }
}