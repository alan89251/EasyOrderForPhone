package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
class ProductModel (
    @ColumnInfo(name = "phoneMake")
    var PhoneMake: String,

    @ColumnInfo(name = "phoneModel")
    var PhoneModel: String,

    @ColumnInfo(name = "phoneColor")
    var PhoneColor: String,

    @ColumnInfo(name = "storageCapacity")
    var StorageCapacity: String,

    @ColumnInfo(name = "price")
    var Price: Double,

    @ColumnInfo(name = "photo") // photo encoded in base64
    var Photo: String
)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "productId")
    var ProductId: Int? = null
}