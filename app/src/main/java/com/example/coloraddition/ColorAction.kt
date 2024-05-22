package com.example.coloraddition

sealed class ColorAction {
    // Num digits changes to 6
    data class FinishEnter(val position:Int, val newColor: String): ColorAction()
    // Num digits changes to a lower number
    data class PartialFill(val position:Int, val newColor: String): ColorAction()
    data class IncorrectInput(val newColor: String): ColorAction()
}