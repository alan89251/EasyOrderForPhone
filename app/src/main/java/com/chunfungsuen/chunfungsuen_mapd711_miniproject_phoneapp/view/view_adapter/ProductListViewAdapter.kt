package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel

class ProductListViewAdapter (context: Context, resource: Int, objects: List<ProductModel>)
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
        itemView.findViewById<TextView>(R.id.product_list_item_price).text = "$" + product!!.Price.toString()
        itemView.findViewById<ImageView>(R.id.product_list_item_photo).setImageBitmap(photo)

        return itemView
    }
}