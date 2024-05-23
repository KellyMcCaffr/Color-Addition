package com.example.coloraddition

import com.example.coloraddition.Constants.DEFAULT_COLOR_SUM
import com.example.coloraddition.Constants.EXPECTED_COLOR_HEX_LENGTH

class ColorViewModel(
    colorHex1: String,
    colorHex2: String,
    sumString: String,
    private val allowedCharacters: String
) {
    private lateinit var changeColorCallback: (Int, String, String) -> Any
    private var state: ColorState
    init {
        state = ColorState(colorHex1, colorHex2, sumString)
    }
    fun setCallback(callback: (Int, String, String) -> Any) {
        changeColorCallback = callback
    }

    fun getColor1(): String {
        return state.colorHex1
    }

    fun getColor2(): String {
        return state.colorHex2
    }

    fun processIntent(intent: ColorIntent) {
        processAction(intentToAction(intent))
    }

    private fun intentToAction(intent: ColorIntent): ColorAction {
        return when (intent) {
            is ColorIntent.ChangeField -> if (!ViewModelUtils.isValidInput(intent.newColor, allowedCharacters)) {
                ColorAction.IncorrectInput(intent.newColor)
            } else if (intent.newColor.length == EXPECTED_COLOR_HEX_LENGTH) {
                ColorAction.FinishEnter(intent.position, intent.newColor, intent.otherColor)
            } else {
                ColorAction.PartialFill(intent.position, intent.newColor, intent.otherColor)
            }
            is ColorIntent.Clear ->
                ColorAction.Clear
        }
    }

    private fun processAction(action: ColorAction) {
        when (action) {
            is ColorAction.FinishEnter ->
                selectColor(action.position, action.newColor, action.otherColor)
            is ColorAction.PartialFill ->
                deselectColor(action.position, action.newColor, action.otherColor)
            is ColorAction.IncorrectInput ->
                callbackForIncorrectInput(action.newColor.length <= EXPECTED_COLOR_HEX_LENGTH)
            is ColorAction.Clear ->
                handleClearAction()
        }
    }

    private fun selectColor(
        position: Int,
        colorHex: String,
        otherColorHex: String
    ) {
        var colorSum = colorHex
        if (ViewModelUtils.canCalculateColorSum(colorHex, otherColorHex)) {
            colorSum = ViewModelUtils.calculateColorSum(colorHex, otherColorHex)
        }
        updateColorState(position, colorHex, otherColorHex, colorSum)
        changeColorCallback(position, colorHex, colorSum)
    }

    private fun deselectColor(
        position: Int,
        colorHex: String,
        otherColorHex: String
    ) {
        var colorSum = DEFAULT_COLOR_SUM
        if (otherColorHex.length == EXPECTED_COLOR_HEX_LENGTH) {
            colorSum = otherColorHex
        }
        updateColorState(position, colorHex, otherColorHex, colorSum)
        changeColorCallback(position, colorHex, colorSum)
    }

    private fun handleClearAction() {
        updateColorState(0, "", "", "")
        changeColorCallback(0, "", DEFAULT_COLOR_SUM)
        changeColorCallback(1, "", DEFAULT_COLOR_SUM)
    }

    private fun callbackForIncorrectInput(charactersIncorrect: Boolean) {
        var errorCode = -2
        if (charactersIncorrect) {
            errorCode = -1
        }
        changeColorCallback(errorCode, "", "")
    }

    private fun updateColorState(
        position: Int,
        colorHex: String,
        otherColorHex: String,
        newColorSum: String
    ) {
        var hex1 = state.colorHex1
        var hex2 = state.colorHex2
        if (position == 0) {
            hex1 = colorHex.lowercase()
            hex2 = otherColorHex
        } else if (position == 1) {
            hex2 = colorHex.lowercase()
            hex1 = otherColorHex
        }
        state = ColorState(hex1, hex2, newColorSum.lowercase())
    }
}