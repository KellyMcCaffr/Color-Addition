package com.example.coloraddition

sealed class ColorAction {
    // Num digits moves to 6
    data class FinishEnterNew(val position:Int, val newColor: String): ColorAction()
    // Num digits moves from 6 to 5
    data class PartialFill(val position:Int, val newColor: String): ColorAction()
}