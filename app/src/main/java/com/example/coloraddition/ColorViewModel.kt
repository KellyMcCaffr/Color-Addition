package com.example.coloraddition

import com.example.coloraddition.Constants.ADD_RESULT_CODE_INCOMPLETE
import com.example.coloraddition.Constants.ADD_RESULT_CODE_SUCCESS
import com.example.coloraddition.Constants.DEFAULT_COLOR_SUM
import com.example.coloraddition.Constants.ERROR_CODE_INVALID_INPUT
import com.example.coloraddition.Constants.ERROR_CODE_TOO_LARGE
import com.example.coloraddition.Constants.EXPECTED_COLOR_HEX_LENGTH

class ColorViewModel(
    colorHex1: String,
    colorHex2: String,
    sumString: String,
    private val allowedCharacters: String
) {
    private lateinit var callbackToAddColors: (Int, String, String) -> Any
    private var state: ColorState
    init {
        state = ColorState(colorHex1, colorHex2, sumString)
    }
    fun setAddColorsCallback(callback: (Int, String, String) -> Any) {
        callbackToAddColors = callback
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
            is ColorIntent.Save ->
                ColorAction.Save(state.colorHex1, state.colorHex2, state.colorSum)
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
            is ColorAction.Save ->
                handleSaveAction()
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
        callbackToAddColors(position, colorHex, colorSum)
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
        callbackToAddColors(position, colorHex, colorSum)
    }

    private fun handleClearAction() {
        updateColorState(0, "", "", "")
        callbackToAddColors(0, "", DEFAULT_COLOR_SUM)
        callbackToAddColors(1, "", DEFAULT_COLOR_SUM)
    }

    private fun handleSaveAction() {
        if (ViewModelUtils.canCalculateColorSum(state.colorHex1, state.colorHex2)) {
            callbackToAddColors(ADD_RESULT_CODE_SUCCESS, "", "")
        } else {
            callbackToAddColors(ADD_RESULT_CODE_INCOMPLETE, "", "")
        }
    }

    private fun callbackForIncorrectInput(charactersIncorrect: Boolean) {
        var errorCode = ERROR_CODE_TOO_LARGE
        if (charactersIncorrect) {
            errorCode = ERROR_CODE_INVALID_INPUT
        }
        callbackToAddColors(errorCode, "", "")
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