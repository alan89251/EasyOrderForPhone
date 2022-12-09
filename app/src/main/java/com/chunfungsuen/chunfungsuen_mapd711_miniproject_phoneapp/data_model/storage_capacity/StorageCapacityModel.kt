package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "storage_capacity")
class StorageCapacityModel(
    @ColumnInfo(name = "productId")
    var ProductId: Int,

    @ColumnInfo(name = "storage_capacity")
    var StorageCapacity: String
)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}