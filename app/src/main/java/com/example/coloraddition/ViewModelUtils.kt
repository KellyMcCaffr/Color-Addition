package com.example.coloraddition

import android.graphics.Color
import com.example.coloraddition.Constants.EXPECTED_COLOR_HEX_LENGTH

object ViewModelUtils {

    fun calculateColorSum(
        colorHex1: String,
        colorHex2: String
    ): String {
        val c1 = Color.parseColor("#$colorHex1")
        val c2 = Color.parseColor("#$colorHex2")
        return Integer.toHexString(mixTwoColors(c1, c2, 0.5f))
    }

    fun isValidInput(
        colorHex: String,
        allowedCharacters: String
    ): Boolean {
        return colorHex.none { !allowedCharacters.contains(it) }
            && colorHex.length <= EXPECTED_COLOR_HEX_LENGTH
    }

    fun hasSum(
        colorHex1: String,
        colorHex2: String
    ): Boolean {
        return colorHex1.length == EXPECTED_COLOR_HEX_LENGTH
            && colorHex2.length == EXPECTED_COLOR_HEX_LENGTH
    }

    fun canCalculateColorSum(
        colorHex1: String,
        colorHex2: String
    ): Boolean {
        return colorHex1.length == EXPECTED_COLOR_HEX_LENGTH
            && colorHex2.length == EXPECTED_COLOR_HEX_LENGTH
    }

    // This method was copied from StackOverflow and converted to Kotlin
    // https://stackoverflow.com/questions/6070163/color-mixing-in-android
    private fun mixTwoColors(color1: Int, color2: Int, amount: Float): Int {
        val ALPHA_CHANNEL: Byte = 24
        val RED_CHANNEL: Byte = 16
        val GREEN_CHANNEL: Byte = 8
        val BLUE_CHANNEL: Byte = 0
        val inverseAmount = 1.0f - amount
        val a =
            ((color1 shr ALPHA_CHANNEL.toInt() and 0xff).toFloat() * amount + (color2 shr ALPHA_CHANNEL.toInt() and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val r =
            ((color1 shr RED_CHANNEL.toInt() and 0xff).toFloat() * amount + (color2 shr RED_CHANNEL.toInt() and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val g =
            ((color1 shr GREEN_CHANNEL.toInt() and 0xff).toFloat() * amount + (color2 shr GREEN_CHANNEL.toInt() and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val b =
            ((color1 and 0xff).toFloat() * amount + (color2 and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        return a shl ALPHA_CHANNEL.toInt() or (r shl RED_CHANNEL.toInt()) or (g shl GREEN_CHANNEL.toInt()) or (b shl BLUE_CHANNEL.toInt())
    }
}