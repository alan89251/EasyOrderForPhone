package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.logic

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity.OrderActivity
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity.UpdateCustomerActivity
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity.UpdateOrderActivity
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.activity.WishListActivity

/**
 * Handle the activity navigation of the menu
 */
class MenuOnSelectHandler(
    private val menuItemIdOfCurActivity: Int?,
    private val context: Context
) {
    /**
     * Create intent for navigating to the activity of the selected menu item
     * @return the intent for navigating to the activity of the selected menu item
     * or null if the selected activity is the same as the current one
     */
    fun createIntent(item: MenuItem): Intent? {
        return if (item.itemId == menuItemIdOfCurActivity) {
            null
        } else {
            when (item.itemId) {
                R.id.menu_phone_model_list -> Intent(context, OrderActivity::class.java)
                R.id.menu_update_order -> Intent(context, UpdateOrderActivity::class.java)
                R.id.menu_update_customer_info -> Intent(context, UpdateCustomerActivity::class.java)
                R.id.menu_wish_list -> Intent(context, WishListActivity::class.java)
                else -> null
            }
        }
    }
}