package com.example.coloraddition.AddColors

sealed class AddColorsAction {
    // Num digits changes to 6
    data class FinishEnter(
        val position:Int,
        val newColor: String,
        val otherColor: String
    ): AddColorsAction()
    // Num digits changes to a lower number
    data class PartialFill(
        val position:Int,
        val newColor: String,
        val otherColor: String
    ): AddColorsAction()
    // Input is incorrect or too large
    data class IncorrectInput(val newColor: String): AddColorsAction()
    object Clear: AddColorsAction()
    data class Save(
        val hex1: String,
        val hex2: String,
        val colorSum: String
    ): AddColorsAction()
}