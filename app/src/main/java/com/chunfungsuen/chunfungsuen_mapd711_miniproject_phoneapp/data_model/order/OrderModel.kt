package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order")
class OrderModel (
    @ColumnInfo(name = "custId")
    var CustId: Int,

    @ColumnInfo(name = "productId")
    var ProductId: Int,

    @ColumnInfo(name = "orderDate")
    var OrderDate: String,

    @ColumnInfo(name = "status")
    var Status: String
)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "orderId")
    var OrderId: Int? = null
}