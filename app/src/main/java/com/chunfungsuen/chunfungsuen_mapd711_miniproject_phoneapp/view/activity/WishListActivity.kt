package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.logic.MenuOnSelectHandler

class WishListActivity : AppCompatActivity() {
    private lateinit var menuOnSelectHandler: MenuOnSelectHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_list)

        menuOnSelectHandler = MenuOnSelectHandler(R.id.menu_wish_list, this)
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