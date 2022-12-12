package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter

import android.content.Context
import android.view.View
import android.widget.ImageButton
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel

class WishListListViewAdapter(
    private val isOnWishList: (Int) -> Boolean,
    private val addToWishList: (Int) -> Unit,
    private val removeFromWishList: (Int) -> Unit,
    private val onRemoveListItem: (ProductModel) -> Unit, // arg: position
    context: Context,
    resource: Int,
    objects: List<ProductModel>
)
    : ProductListViewAdapter(
        isOnWishList,
        addToWishList,
        removeFromWishList,
        context,
        resource,
        objects
    )
{
    /**
     * @param itemView the view of the layout of the list item
     * @param product the product shown on this list item
     */
    override fun configButtons(itemView: View, product: ProductModel) {
        val button = itemView.findViewById<ImageButton>(R.id.product_list_item_btn)
        button.setImageResource(R.drawable.bookmark_x_fill)
        button.setOnClickListener {
            // remove the product from the wish list of the customer
            removeFromWishList(product.ProductId!!)
            // remove the list item from the wish list view
            onRemoveListItem(product)
        }
    }
}