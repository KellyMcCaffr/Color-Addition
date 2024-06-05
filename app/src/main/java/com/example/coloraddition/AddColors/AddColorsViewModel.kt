package com.example.coloraddition.AddColors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coloraddition.Constants.ADD_RESULT_CODE_DELETE_INCOMPLETE
import com.example.coloraddition.Constants.ADD_RESULT_CODE_DELETE_SUCCESS
import com.example.coloraddition.Constants.ADD_RESULT_CODE_SAVE_DUPLICATE
import com.example.coloraddition.Constants.ADD_RESULT_CODE_SAVE_INCOMPLETE
import com.example.coloraddition.Constants.ADD_RESULT_CODE_SAVE_SUCCESS
import com.example.coloraddition.Constants.DEFAULT_COLOR_SUM
import com.example.coloraddition.Constants.ERROR_CODE_INVALID_INPUT
import com.example.coloraddition.Constants.ERROR_CODE_TOO_LARGE
import com.example.coloraddition.Constants.EXPECTED_COLOR_HEX_LENGTH
import com.example.coloraddition.SavedColors.SavedColor
import com.example.coloraddition.SavedColorsDatabase
import com.example.coloraddition.ViewModelUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddColorsViewModel(
    colorHex1: String,
    colorHex2: String,
    sumString: String,
    private val allowedCharacters: String,
    private val db: SavedColorsDatabase
): ViewModel() {
    private lateinit var callbackToAddColors: (Int, String, String) -> Any
    private var state: AddColorsState
    init {
        state = AddColorsState(colorHex1, colorHex2, sumString)
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

    fun getColorSum(): String {
        return state.colorSum
    }

    fun processIntent(intent: AddColorsIntent) {
        processAction(intentToAction(intent))
    }

    fun restoreSavedColor(hex1: String, hex2: String, sum: String) {
        updateColorState(0, hex1, hex2, sum)
    }

    private fun intentToAction(intent: AddColorsIntent): AddColorsAction {
        return when (intent) {
            is AddColorsIntent.ChangeField -> if (!ViewModelUtils.isValidInput(
                intent.newColor, allowedCharacters)
            ) {
                AddColorsAction.IncorrectInput(intent.newColor)
            } else if (intent.newColor.length == EXPECTED_COLOR_HEX_LENGTH) {
                AddColorsAction.FinishEnter(intent.position, intent.newColor, intent.otherColor)
            } else {
                AddColorsAction.PartialFill(intent.position, intent.newColor, intent.otherColor)
            }
            is AddColorsIntent.Clear ->
                AddColorsAction.Clear
            is AddColorsIntent.Save ->
                AddColorsAction.Save(state.colorHex1, state.colorHex2, state.colorSum)
            is AddColorsIntent.DeleteFromSaved ->
                AddColorsAction.DeleteFromSaved(state.colorHex1, state.colorHex2, state.colorSum)
        }
    }

    private fun processAction(action: AddColorsAction) {
        when (action) {
            is AddColorsAction.FinishEnter ->
                selectColor(action.position, action.newColor, action.otherColor)
            is AddColorsAction.PartialFill ->
                deselectColor(action.position, action.newColor, action.otherColor)
            is AddColorsAction.IncorrectInput ->
                callbackForIncorrectInput(action.newColor.length <= EXPECTED_COLOR_HEX_LENGTH)
            is AddColorsAction.Clear ->
                handleClearAction()
            is AddColorsAction.Save ->
                handleSaveAction()
            is AddColorsAction.DeleteFromSaved ->
                handleDeleteFromSavedAction()
        }
    }

    private fun selectColor(
        position: Int,
        colorHex: String,
        otherColorHex: String
    ) {
        var colorSum = colorHex
        if (ViewModelUtils.isFullInput(colorHex, otherColorHex)) {
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
        if (ViewModelUtils.isFullInput(state.colorHex1, state.colorHex2)) {
            CoroutineScope(Dispatchers.IO).launch {
                val currentSavedColors = db.SavedColorDao().getAll()
                val id = ViewModelUtils.generateUniqueID()
                val newSavedColor = SavedColor(id, state.colorHex1, state.colorHex2, state.colorSum)
                if (!ViewModelUtils.isColorAlreadySaved(newSavedColor, currentSavedColors)) {
                    db.SavedColorDao().insertAll(newSavedColor)
                    viewModelScope.launch {
                        callbackToAddColors(ADD_RESULT_CODE_SAVE_SUCCESS, "", "")
                    }
                } else {
                    viewModelScope.launch {
                        callbackToAddColors(ADD_RESULT_CODE_SAVE_DUPLICATE, "", "")
                    }
                }
            }
        } else {
            callbackToAddColors(ADD_RESULT_CODE_SAVE_INCOMPLETE, "", "")
        }
    }

    private fun handleDeleteFromSavedAction() {
        CoroutineScope(Dispatchers.IO).launch {
            if (ViewModelUtils.isFullInput(state.colorHex1, state.colorHex2)) {
                val currentSavedColor = SavedColor(ViewModelUtils.generateUniqueID(),
                    state.colorHex1, state.colorHex2, state.colorSum)
                db.SavedColorDao().deleteColor(currentSavedColor)
                viewModelScope.launch {
                    callbackToAddColors(ADD_RESULT_CODE_DELETE_SUCCESS, "", "")
                }
            } else {
                viewModelScope.launch {
                    callbackToAddColors(ADD_RESULT_CODE_DELETE_INCOMPLETE, "", "")
                }
            }
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
        state = AddColorsState(hex1, hex2, newColorSum.lowercase())
    }
}