package com.example.coloraddition

import com.example.coloraddition.Constants.EXPECTED_COLOR_HEX_LENGTH

class ColorViewModel(var state: ColorState) {
    fun processIntent(intent: ColorIntent) {
        processAction(intentToAction(intent))
    }

    private fun updateColorValue(
        position: Int,
        colorHex: String
    ) {
        if (colorHex.length == EXPECTED_COLOR_HEX_LENGTH || colorHex.isEmpty()) {
            var hex1 = state.colorHex1
            var hex2 = state.colorHex2
            if (position == 0) {
                hex1 = colorHex
            } else if (position == 1) {
                hex2 = colorHex
            }
            state = ColorState(hex1, hex2, "0xFF$colorHex")
        }
    }

    private fun intentToAction(intent: ColorIntent): ColorAction {
        return when (intent) {
            is ColorIntent.ChangeField -> if (intent.newColor.length == EXPECTED_COLOR_HEX_LENGTH) {
                ColorAction.Select(intent.position, intent.newColor)
            } else {
                ColorAction.Deselect(intent.position, intent.newColor)
            }
        }
    }
    private fun processAction(action: ColorAction) {
        when (action) {
            is ColorAction.Select ->
                selectColor(action.position, action.newColor)
            is ColorAction.Deselect ->
               deselectColor(action.position, action.newColor)
        }
    }
    private fun selectColor(
        position: Int,
        colorHex: String
    ) {
        updateColorValue(position, colorHex)
    }
    private fun deselectColor(
        position: Int,
        colorHex: String
    ) {
        updateColorValue(position, colorHex)
    }
}