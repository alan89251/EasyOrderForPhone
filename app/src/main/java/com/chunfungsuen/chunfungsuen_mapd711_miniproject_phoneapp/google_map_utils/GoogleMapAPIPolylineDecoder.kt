package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.google_map_utils

import com.google.android.gms.maps.model.LatLng

class GoogleMapAPIPolylineDecoder {
    /**
     * decode LatLng pairs from string
     */
    fun decode(encodedStr: String): List<LatLng> {
        var lagLngPairs = ArrayList<LatLng>()
        var singleDecimalStr = ""
        var isLat = true // Lat comes first
        var lat = 0.0
        var lng = 0.0
        var latLng = LatLng(0.0, 0.0)
        for (c in encodedStr) {
            singleDecimalStr += c
            if ((c.toInt() - 63) < 0x20) {
                if (isLat) {
                    lat += decodeSingleDecimal(singleDecimalStr)
                }
                else {
                    lng += decodeSingleDecimal(singleDecimalStr)
                    lagLngPairs.add(LatLng(lat, lng))
                }
                singleDecimalStr = ""
                isLat = !isLat
            }
        }

        return lagLngPairs
    }

    /**
     * decode decimal from string
     */
    fun decodeSingleDecimal(encodedStr: String): Double {
        var decArray: Array<UInt> = convertASCIIToDecimal(encodedStr)
        subtractForEachElement(decArray, 63u)
        var byteArray: Array<UByte> = convertToByteArray(decArray)
        xorForNotLastElements(byteArray, 0x20u)
        byteArray.reverse()
        val unsignedNum = combineToOneDecimalNum(byteArray)
        var twoComplementNum = convertToTwoComplement(unsignedNum)
        var signedNum = convertTwoComplementToSignedInt(twoComplementNum)
        return signedNum.toDouble() / 100000.0
    }

    /**
     * Convert the string from ASCII to array of decimals
     */
    private fun convertASCIIToDecimal(encodedStr: String): Array<UInt> {
        return Array<UInt>(encodedStr.length) {
            encodedStr[it].toInt().toUInt()
        }
    }

    /**
     * Subtract specified value for each element in the decimal array
     */
    private fun subtractForEachElement(decArray: Array<UInt>, valueToSubtract: UInt) {
        for (i in decArray.indices) {
            decArray[i] = decArray[i] - valueToSubtract
        }
    }

    /**
     * Convert decimal array to byte array
     */
    private fun convertToByteArray(decArray: Array<UInt>): Array<UByte> {
        return Array<UByte>(decArray.size) {
            decArray[it].toUByte()
        }
    }

    /**
     * XOR each value with specified mask if another bit chunk follows
     */
    private fun xorForNotLastElements(byteArray: Array<UByte>, mask: UByte) {
        for (i in byteArray.indices) {
            if (i != byteArray.lastIndex) {
                byteArray[i] = byteArray[i] xor mask
            }
        }
    }

    /**
     * Combine the elements into one decimal number
     */
    private fun combineToOneDecimalNum(byteArray: Array<UByte>): UInt {
        var result: UInt = 0u
        for (i in byteArray.indices) {
            result = result shl 5
            result += byteArray[i]
        }
        return result
    }

    /**
     * Convert to Two's complement form of the original number
     */
    private fun convertToTwoComplement(num: UInt): UInt {
        return if ((num and 1u) == 1u) { // last bit is '1'
            (num shr 1) xor 0xffffffffu // right shift 1 bit and then invert
        } else {
            num shr 1 // right shift 1 bit
        }
    }

    /**
     * Convert Two's complement to signed integer
     */
    private fun convertTwoComplementToSignedInt(num: UInt): Int {
        val isNegative = (num and 0x80000000u) != 0u
        return if (isNegative) {
            ((num - 1u) xor 0xffffffffu) // minus 1 and then invert
                .toInt() * -1 // turn to negative signed number
        } else {
            num.toInt()
        }
    }
}