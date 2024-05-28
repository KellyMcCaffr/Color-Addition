package com.example.coloraddition

sealed class ColorIntent {
    data class ChangeField(
        val position:Int,
        val newColor: String,
        val otherColor: String
    ): ColorIntent()

    object Clear: ColorIntent()

    object Save: ColorIntent()
}