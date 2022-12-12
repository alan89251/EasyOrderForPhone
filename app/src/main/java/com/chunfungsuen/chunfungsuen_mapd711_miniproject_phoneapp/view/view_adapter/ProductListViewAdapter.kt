package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel

open class ProductListViewAdapter (
    private val isOnWishList: (Int) -> Boolean,
    private val addToWishList: (Int) -> Unit,
    private val removeFromWishList: (Int) -> Unit,
    private val onSelect: (ProductModel) -> Unit, // event handler when the product is selected
    context: Context,
    resource: Int,
    objects: List<ProductModel>)
    : ArrayAdapter<ProductModel>(context, resource, objects)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // inflate item view
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View = inflater.inflate(R.layout.product_list_item_layout, parent, false)

        val product = getItem(position)

        // decode the photo from base64
        val photoBase64Str = product!!.Photo
        val photoByteArray = Base64.decode(photoBase64Str, Base64.DEFAULT)
        val photo = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.size)

        // config item view
        itemView.findViewById<TextView>(R.id.product_list_item_phone_model).text = product!!.PhoneModel
        itemView.findViewById<TextView>(R.id.product_list_item_product_id).text = product!!.ProductId.toString()
        itemView.findViewById<TextView>(R.id.product_list_item_phone_make).text = product!!.PhoneMake
        itemView.findViewById<TextView>(R.id.product_list_item_phone_color).text = product!!.PhoneColor
        itemView.findViewById<TextView>(R.id.product_list_item_storage_capacity).text = product!!.StorageCapacity
        itemView.findViewById<TextView>(R.id.product_list_item_price).text = "$" + product!!.Price.toString()
        itemView.findViewById<ImageView>(R.id.product_list_item_photo).setImageBitmap(photo)

        configButtons(itemView, product!!)

        return itemView
    }

    /**
     * @param itemView the view of the layout of the list item
     * @param product the product shown on this list item
     */
    protected open fun configButtons(itemView: View, product: ProductModel) {
        // set wish list button as add to wish list or remove from wish list
        // depends on whether the item is on the wish list
        if (isOnWishList(product.ProductId!!)) {
            setButtonAsRemoveFromWishList(
                product,
                itemView.findViewById<ImageButton>(R.id.product_list_item_wish_list_btn)
            )
        }
        else {
            setButtonAsAddToWishList(
                product,
                itemView.findViewById<ImageButton>(R.id.product_list_item_wish_list_btn)
            )
        }

        setSelectBtn(product, itemView.findViewById<ImageButton>(R.id.product_list_item_select_btn))
    }

    /**
     * Set button icon and onClickListener
     */
    protected open fun setButtonAsAddToWishList(product: ProductModel, button: ImageButton) {
        button.setImageResource(R.drawable.bookmark)
        // 1. Add product to wish list
        // 2. Change the button to "remove from wish list button"
        button.setOnClickListener {
            addToWishList(product.ProductId!!)
            setButtonAsRemoveFromWishList(product, button)
        }
    }

    /**
     * Set button icon and onClickListener
     */
    protected open fun setButtonAsRemoveFromWishList(product: ProductModel, button: ImageButton) {
        button.setImageResource(R.drawable.bookmark_check_fill)
        // 1. Remove product from wish list
        // 2. Change the button to "add from wish list button"
        button.setOnClickListener {
            removeFromWishList(product.ProductId!!)
            setButtonAsAddToWishList(product, button)
        }
    }

    /**
     * set the button of selecting the product to buy
     */
    protected open fun setSelectBtn(product: ProductModel, button: ImageButton) {
        button.setImageResource(R.drawable.cart_plus)
        button.setOnClickListener {
            onSelect(product)
        }
    }
}