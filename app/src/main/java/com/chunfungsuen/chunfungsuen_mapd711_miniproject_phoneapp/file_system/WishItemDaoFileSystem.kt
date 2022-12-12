package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.file_system

import android.content.Context
import android.util.Log
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item.WishItemDao
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.wish_item.WishItemModel
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class WishItemDaoFileSystem: WishItemDao {
    private val context: Context
    private val storageDir: String // directory to store the wish lists

    constructor(context: Context, storageDir: String) {
        this.context = context
        this.storageDir = storageDir

        // create directory if not exist
        val dir = File(context.filesDir.path + "/" + this.storageDir)
        if (!dir.exists()) {
            dir.mkdir()
        }
    }

    override fun getAllWishItems(customerId: Int): List<WishItemModel> {
        // file name of the wish list of a customer is the customer id
        val fileName = customerId.toString() + ".txt"
        val file = File(context.filesDir.path + "/" + storageDir, fileName)
        if (!file.exists()) {
            return ArrayList()
        }

        val wishItems = ArrayList<WishItemModel>()
        val input = file.inputStream()
        var fileContent: String
        input.use {
            var buffer = StringBuilder()
            var bytes_read = input.read()
            while (bytes_read != -1) {
                buffer.append(bytes_read.toChar())
                bytes_read = input.read()
            }
            fileContent = buffer.toString()
        }

        if (fileContent.isEmpty()) {
            return wishItems
        }

        for (line in fileContent.trimEnd('\n')
                .split("\n")
        ) {
            wishItems.add(WishItemModel(line.toInt()))
        }

        return wishItems
    }

    /**
     * Truncate all existing wish items for customer
     * Then save the new wish items
     */
    override fun truncateExistAndSaveWishItemsForCustomer(
        customerId: Int,
        wishItems: List<WishItemModel>
    ) {
        // file name of the wish list of a customer is the customer id
        val fileName = customerId.toString() + ".txt"
        val file = File(context.filesDir.path + "/" + storageDir, fileName)
        file.createNewFile() // create file if not exist
        val output = file.outputStream()
        output.use {
            for (wishItem in wishItems) {
                output.write(wishItem.ProductId.toString().toByteArray())
                output.write('\n'.toInt())
            }
        }
    }
}