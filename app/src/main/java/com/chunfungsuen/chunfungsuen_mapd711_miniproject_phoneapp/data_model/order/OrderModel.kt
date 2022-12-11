package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "order")
class OrderModel (
    @ColumnInfo(name = "custId")
    var CustId: Int,

    @ColumnInfo(name = "productId")
    var ProductId: Int,

    @ColumnInfo(name = "colour")
    var Colour: String,

    @ColumnInfo(name = "storageCapacity")
    var StorageCapacity: String,

    @ColumnInfo(name = "orderDate")
    var OrderDate: String,

    @ColumnInfo(name = "storeName")
    var StoreName: String,

    @ColumnInfo(name = "storePlaceId")
    var StorePlaceId: String,

    @ColumnInfo(name = "status")
    var Status: String
) : Serializable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "orderId")
    var OrderId: Int? = null
}