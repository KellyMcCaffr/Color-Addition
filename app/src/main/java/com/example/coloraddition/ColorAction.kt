package com.example.coloraddition

sealed class ColorAction {
    // Num digits moves to 6 in some EditText
    data class Select(val position:Int, val newColor: String): ColorAction() {

    }
    // Digits move from 6 to 5 in an EditText
    data class Deselect(val position:Int, val newColor: String): ColorAction()
}