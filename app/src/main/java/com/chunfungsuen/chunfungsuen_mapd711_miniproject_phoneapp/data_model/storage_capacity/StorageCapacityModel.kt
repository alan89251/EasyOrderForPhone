package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
@Entity(tableName = "storage_capacity")
class StorageCapacityModel(
    @ColumnInfo(name = "product_id")
    var ProductId: Int,

    @ColumnInfo(name = "storage_capacity")
    var StorageCapacity: String
)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}