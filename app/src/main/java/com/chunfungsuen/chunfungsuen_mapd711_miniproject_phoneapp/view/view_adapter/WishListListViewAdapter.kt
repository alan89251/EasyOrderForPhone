package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter

import android.content.Context
import android.view.View
import android.widget.ImageButton
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
class WishListListViewAdapter(
    private val isOnWishList: (Int) -> Boolean,
    private val addToWishList: (Int) -> Unit,
    private val removeFromWishList: (Int) -> Unit,
    private val onSelect: (ProductModel) -> Unit, // event handler when the product is selected,
    private val onRemoveListItem: (ProductModel) -> Unit, // arg: position
    context: Context,
    resource: Int,
    objects: List<ProductModel>
)
    : ProductListViewAdapter(
        isOnWishList,
        addToWishList,
        removeFromWishList,
        onSelect,
        context,
        resource,
        objects
    )
{
    /**
     * Set button icon and onClickListener
     */
    override fun setButtonAsRemoveFromWishList(product: ProductModel, button: ImageButton) {
        button.setImageResource(R.drawable.bookmark_x_fill)
        button.setOnClickListener {
            // remove the product from the wish list of the customer
            removeFromWishList(product.ProductId!!)
            // remove the list item from the wish list view
            onRemoveListItem(product)
        }
    }
}