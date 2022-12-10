package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phone_price")
class PhonePriceModel(
    @ColumnInfo(name = "product_id")
    var ProductId: Int,

    @ColumnInfo(name = "storage_capacity_id")
    var StorageCapacityId: Int,

    @ColumnInfo(name = "price")
    var Price: Double
)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}