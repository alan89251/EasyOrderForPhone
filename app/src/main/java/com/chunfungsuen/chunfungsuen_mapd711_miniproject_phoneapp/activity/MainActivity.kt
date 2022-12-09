package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * Navigate to the login activity
     */
    fun navigateToLogin(v: View) {
        var intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
    }
}