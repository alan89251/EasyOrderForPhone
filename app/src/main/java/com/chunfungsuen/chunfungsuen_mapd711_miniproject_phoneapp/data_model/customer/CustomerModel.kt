package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Group members:
 * 1. Chun Fung Suen (301277969)
 */
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
    var PostalCode: String,

    @ColumnInfo(name = "telephone")
    var Telephone: String,

    @ColumnInfo(name = "creditCardNo")
    var CreditCardNo: String,

    @ColumnInfo(name = "cardType")
    var CardType: String,

    @ColumnInfo(name = "cardExpiration")
    var CardExpiration: String,

    @ColumnInfo(name = "creditCardCVC")
    var CreditCardCVC: String
)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "custId")
    var CustId: Int? = null

    companion object {
        fun isValidUserName(input: String): Boolean {
            return input.isNotEmpty()
        }

        fun isValidPassword(input: String): Boolean {
            return input.isNotEmpty()
        }

        /**
         * Firstname should contain alphabet and whitespace only
         */
        fun isValidFirstname(input: String): Boolean {
            return input.matches(Regex("[a-zA-Z\\s]+"))
        }

        /**
         * Lastname should contain alphabet and whitespace only
         */
        fun isValidLastname(input: String): Boolean {
            return input.matches(Regex("[a-zA-Z\\s]+"))
        }

        fun isValidAddress(input: String): Boolean {
            return input.isNotEmpty()
        }

        /**
         * City should contain alphabet and whitespace only
         */
        fun isValidCity(input: String): Boolean {
            return input.matches(Regex("[a-zA-Z\\s]+"))
        }

        /**
         * Country should contain alphabet and whitespace only
         */
        fun isValidCountry(input: String): Boolean {
            return input.matches(Regex("[a-zA-Z\\s]+"))
        }

        /**
         * Postal code should contain alphabet and number only. Its length must be 6.
         * The format of it can be "XXXXXX", or with a whitespace "XXX XXX"
         */
        fun isValidPostalCode(input: String): Boolean {
            return input.matches(Regex("[a-zA-Z0-9]{6}"))
                    || input.matches(Regex("[a-zA-Z0-9]{3}\\s[a-zA-Z0-9]{3}"))
        }

        fun isValidTelephone(input: String): Boolean {
            return input.isNotEmpty()
        }

        fun isValidCreditCardNo(input: String): Boolean {
            return input.isNotEmpty()
        }

        /**
         * Credit card type should contain alphabet and whitespace only
         */
        fun isValidCardType(input: String): Boolean {
            return input.matches(Regex("[a-zA-Z\\s]+"))
        }

        /**
         * validate credit card expiration date. Credit card expiration date should either in
         * yyyy/MM/dd, yyyy/M/dd, yyyy/MM/d or yyyy/M/d
         * @param input date string of the credit card expiration
         */
        fun isValidCardExpiration(input: String): Boolean {
            // determine the pattern of the date string
            val pattern = if (input.matches(Regex("[0-9]{4}/[0-9]{2}/[0-9]{2}"))) {
                "yyyy/MM/dd"
            }
            else if (input.matches(Regex("[0-9]{4}/[0-9]/[0-9]{2}"))) {
                "yyyy/M/dd"
            }
            else if (input.matches(Regex("[0-9]{4}/[0-9]{2}/[0-9]"))){
                "yyyy/MM/d"
            }
            else if (input.matches(Regex("[0-9]{4}/[0-9]/[0-9]"))) {
                "yyyy/M/d"
            }
            else {
                return false
            }

            // use LocalDate to parse the string. If it throws exception, mean the date format is invalid
            try {
                LocalDate.parse(input, DateTimeFormatter.ofPattern(pattern))
            }
            catch(e: DateTimeParseException) {
                return false
            }

            return true
        }

        /**
         * Credit card CVV/CVC must be 3 digits
         */
        fun isValidCreditCardCVC(input: String): Boolean {
            return input.matches(Regex("[0-9]{3}"))
        }
    }
}