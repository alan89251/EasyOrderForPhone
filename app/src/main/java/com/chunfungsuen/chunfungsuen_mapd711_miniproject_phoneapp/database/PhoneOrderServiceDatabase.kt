package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerDao
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderDao
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.order.OrderModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour.PhoneColourDao
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_colour.PhoneColourModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceDao
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.phone_price.PhonePriceModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductDao
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.product.ProductModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity.StorageCapacityDao
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.storage_capacity.StorageCapacityModel

@Database(
    entities = arrayOf(
        CustomerModel::class,
        ProductModel::class,
        OrderModel::class,
        StorageCapacityModel::class,
        PhoneColourModel::class,
        PhonePriceModel::class
    ),
    version = 3,
    exportSchema = false
)
abstract class PhoneOrderServiceDatabase : RoomDatabase() {
    abstract fun customerDao() : CustomerDao
    abstract fun productDao() : ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun storageCapacityDao(): StorageCapacityDao
    abstract fun phoneColourDao(): PhoneColourDao
    abstract fun phonePriceDao(): PhonePriceDao

    companion object {
        @Volatile
        private var INSTANCE: PhoneOrderServiceDatabase? = null

        // create a database name "phone_order_service_database"
        fun getDatabaseClient(context: Context): PhoneOrderServiceDatabase {
            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(context, PhoneOrderServiceDatabase::class.java, "phone_order_service_database")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!
            }
        }
    }
}