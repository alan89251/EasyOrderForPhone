package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
@Entity(tableName = "phone_colour")
class PhoneColourModel(
    @ColumnInfo(name = "product_id")
    var ProductId: Int,

    @ColumnInfo(name = "colour")
    var Colour: String
)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}