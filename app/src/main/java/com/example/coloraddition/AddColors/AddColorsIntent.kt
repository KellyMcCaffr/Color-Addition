package com.example.coloraddition.AddColors

sealed class AddColorsIntent {
    data class ChangeField(
        val position:Int,
        val newColor: String,
        val otherColor: String
    ): AddColorsIntent()
    object Clear: AddColorsIntent()

    object Save: AddColorsIntent()

    object DeleteFromSaved: AddColorsIntent()
}