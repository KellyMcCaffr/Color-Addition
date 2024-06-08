package com.example.coloraddition.SavedColors

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coloraddition.R
import com.example.coloraddition.SavedColorsDatabase
import com.example.coloraddition.ViewModelUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedColorsViewModel(private val db: SavedColorsDatabase): ViewModel() {
    private lateinit var standardCallbackToScreen: (List<SavedColor>, Boolean) -> Any
    private lateinit var combineCallbackToScreen: (SavedColor, String, String, String) -> Any
    private var isCombineMode = false
    private var selectedCombineColorSum = ""
    fun setSavedColorsCallbacks(
        standardCallback: (List<SavedColor>, Boolean) -> Any,
        combineCallback: (SavedColor, String, String, String) -> Any
    ) {
        standardCallbackToScreen = standardCallback
        combineCallbackToScreen = combineCallback
    }

    fun processIntent(intent: SavedColorsIntent) {
        processAction(intentToAction(intent))
    }

    private fun intentToAction(intent: SavedColorsIntent): SavedColorsAction {
        return when (intent) {
            is SavedColorsIntent.LoadAll -> SavedColorsAction.LoadAll
            is SavedColorsIntent.DeleteAll -> SavedColorsAction.DeleteAll
            is SavedColorsIntent.TapColor ->
                if (isCombineMode) {
                    SavedColorsAction.CombineLocal(intent.tappedColor)
                } else {
                    SavedColorsAction.OpenLocal(intent.tappedColor)
                }
        }
    }

    private fun processAction(action: SavedColorsAction) {
        when (action) {
            is SavedColorsAction.LoadAll ->
                handleLoadSavedColorsAction()
            is SavedColorsAction.DeleteAll ->
                handleDeleteSavedColorsAction()
            is SavedColorsAction.CombineLocal ->
                handleCombineLocalSavedColorsAction(action.newCombineColor)
            is SavedColorsAction.OpenLocal ->
                handleOpenLocalSavedColorsAction(action.colorToOpen)
        }
    }

    fun toggleCombineMode(context: Context) {
        isCombineMode = !isCombineMode
        if (isCombineMode) {
            Toast.makeText(context, context.getString(R.string.save_message_combine_intro),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLoadSavedColorsAction() {
        CoroutineScope(Dispatchers.IO).launch {
            val savedColors = db.SavedColorDao().getAll()
            viewModelScope.launch {
                standardCallbackToScreen(savedColors, false)
            }
        }
    }

    private fun handleDeleteSavedColorsAction() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = db.SavedColorDao()
            dao.deleteAll()
            viewModelScope.launch {
                standardCallbackToScreen(listOf(), false)
            }
        }
    }

    private fun handleCombineLocalSavedColorsAction(
        color: SavedColor
    ) {
        if (selectedCombineColorSum.isEmpty()) {
            selectedCombineColorSum = color.sum
            combineCallbackToScreen(color, "", "", "")
        } else {
            val newColorSum = ViewModelUtils.calculateColorSum(
                color.sum, selectedCombineColorSum)
            combineCallbackToScreen(color, color.sum, selectedCombineColorSum, newColorSum)
        }
    }

    private fun handleOpenLocalSavedColorsAction(
        color: SavedColor
    ) {
        standardCallbackToScreen(listOf(color), true)
    }
}