package com.example.coloraddition

import com.example.coloraddition.Constants.EXPECTED_COLOR_HEX_LENGTH

class ColorViewModel(
    colorHex1: String,
    colorHex2: String,
    sumString: String,
    private val allowedCharacters: String
) {
    private lateinit var changeColorCallback: (Int, String, String) -> Unit
    private var state: ColorState
    fun setCallback(callback: (Int, String, String) -> Unit) {
        changeColorCallback = callback
    }

    fun getColor1(): String {
        return state.colorHex1
    }

    fun getColor2(): String {
        return state.colorHex2
    }

    init {
        state = ColorState(colorHex1, colorHex2, sumString)
    }

    fun processIntent(intent: ColorIntent, defaultColorSum: String) {
        processAction(intentToAction(intent), defaultColorSum)
    }

    private fun intentToAction(intent: ColorIntent): ColorAction {
        return when (intent) {
            is ColorIntent.ChangeField -> if (intent.newColor.length == EXPECTED_COLOR_HEX_LENGTH) {
                ColorAction.FinishEnterNew(intent.position, intent.newColor)
            } else {
                ColorAction.PartialFill(intent.position, intent.newColor)
            }
        }
    }

    private fun processAction(action: ColorAction, defaultColorSum: String) {
        when (action) {
            is ColorAction.FinishEnterNew ->
                selectColor(action.position, action.newColor)

            is ColorAction.PartialFill ->
                deselectColor(action.position, action.newColor, defaultColorSum)
        }
    }

    private fun selectColor(
        position: Int,
        colorHex: String
    ) {
        val otherColorHex = if (position == 0) {
            state.colorHex2
        } else { state.colorHex1 }
        if (ViewModelUtils.isValidInput(colorHex, allowedCharacters)) {
            var colorSum = colorHex
            if (ViewModelUtils.canCalculateColorSum(colorHex, otherColorHex)) {
                colorSum = ViewModelUtils.calculateColorSum(colorHex, otherColorHex)
            }
            updateColorState(position, colorHex, colorSum)
            changeColorCallback(position, colorHex, colorSum)
        }
    }

    private fun deselectColor(
        position: Int,
        colorHex: String,
        defaultColorSum: String
    ) {
        val otherColorHex = if (position == 0) {
            state.colorHex2
        } else { state.colorHex1 }
        if (ViewModelUtils.isValidInput(colorHex, allowedCharacters)) {
            var colorSum = defaultColorSum
            if (otherColorHex.length == EXPECTED_COLOR_HEX_LENGTH) {
                colorSum = otherColorHex
            }
            updateColorState(position, colorHex, colorSum)
            changeColorCallback(position, colorHex, colorSum)
        }
    }

    private fun updateColorState(
        position: Int,
        colorHex: String,
        newColorSum: String
    ) {
        var hex1 = state.colorHex1
        var hex2 = state.colorHex2
        if (position == 0) {
            hex1 = colorHex
        } else if (position == 1) {
            hex2 = colorHex
        }
        state = ColorState(hex1, hex2, newColorSum)
    }
}