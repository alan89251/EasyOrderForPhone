package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer")
data class CustomerModel(
    @ColumnInfo(name = "userName")
    var UserName: String,

    @ColumnInfo(name = "password")
    var Password: String,

    @ColumnInfo(name = "firstname")
    var Firstname: String,

    @ColumnInfo(name = "lastname")
    var Lastname: String,

    @ColumnInfo(name = "address")
    var Address: String,

    @ColumnInfo(name = "city")
    var City: String,

    @ColumnInfo(name = "country")
    var Country: String,

    @ColumnInfo(name = "postalCode")
    var PostalCode: String
)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "custId")
    var CustId: Int? = null
}