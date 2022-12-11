package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.view_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder

class OrderListViewAdapter (
    private val customer: CustomerModel,
    private val getProductByIdAsync: (Int, (ProductModel) -> Unit) -> Unit,
    private val getPriceAsync: (Int, String, (PhonePriceModel) -> Unit) -> Unit,
    private val updateOrder: (OrderModel) -> Unit,
    private val onCheckStoreBtnClicked: (String) -> Unit,
    context: Context,
    resource: Int,
    objects: List<OrderModel>)
    : ArrayAdapter<OrderModel>(context, resource, objects)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // inflate item view
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View = inflater.inflate(R.layout.order_list_item_layout, parent, false)

        val order = getItem(position)!!
        // show order info in UI
        itemView.findViewById<TextView>(R.id.order_list_item_order_id).text = order.OrderId.toString()
        itemView.findViewById<TextView>(R.id.order_list_item_customer_id).text = order.CustId.toString()
        itemView.findViewById<TextView>(R.id.order_list_item_product_id).text = order.ProductId.toString()
        itemView.findViewById<TextView>(R.id.order_list_item_order_date).text = order.OrderDate
        itemView.findViewById<TextView>(R.id.order_list_item_status).text = order.Status
        itemView.findViewById<TextView>(R.id.order_list_item_phone_color).text = order.Colour
        itemView.findViewById<TextView>(R.id.order_list_item_storage_capacity).text = order.StorageCapacity
        // config cancel order button
        val cancelOrderBtn = itemView.findViewById<ImageButton>(R.id.cancel_order_btn)
        cancelOrderBtn.setOnClickListener {
            onCancelButtonClicked(order, itemView)
        }
        // config check store button
        itemView.findViewById<ImageButton>(R.id.check_store_btn)
            .setOnClickListener {
                onCheckStoreBtnClicked(order.StorePlaceId)
            }
        // disable and hide the cancel order button if the order is already cancelled
        if (order.Status == context.resources.getString(R.string.order_status_text_cancelled)) {
            cancelOrderBtn.isEnabled = false
            cancelOrderBtn.isVisible = false
        }

        // get product info from repository and then show them in the UI
        getProductByIdAsync(order.ProductId, { onProductResult(itemView, it) })
        // get phone price from repository and then show it in the UI
        getPriceAsync(order.ProductId, order.StorageCapacity, { onPriceResult(itemView, it) })

        return itemView
    }

    /**
     * Show product info in UI
     */
    private fun onProductResult(itemView: View, product: ProductModel) {
        itemView.findViewById<TextView>(R.id.order_list_item_phone_model).text = product.PhoneModel
        itemView.findViewById<TextView>(R.id.order_list_item_phone_make).text = product.PhoneMake
    }

    /**
     * Show phone price in UI
     */
    private fun onPriceResult(itemView: View, price: PhonePriceModel) {
        itemView.findViewById<TextView>(R.id.order_list_item_price).text = "$" + price.Price.toString()
    }

    private fun onCancelButtonClicked(order: OrderModel, itemView: View) {
        val currentDate = LocalDateTime.now() // get current date
        // parse the order date
        val orderDate = LocalDateTime.from(
            DateTimeFormatterBuilder()
                .appendPattern("dd/MM/yyyy HH:mm:ss")
                .toFormatter()
                .parse(
                    itemView.findViewById<TextView>(R.id.order_list_item_order_date).text.toString() + " 00:00:00"
                )
        )
        // Only allow to cancel at least 24 hours before the order date
        if (currentDate.isAfter(
                orderDate.minusHours(24)
            ))
        {
            Toast.makeText(context, "Only allow to cancel at least 24 hours before the order date", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Cancel the order which change its status to "Cancelled"
        order.Status = context.resources.getString(R.string.order_status_text_cancelled)
        updateOrder(order)
    }
}