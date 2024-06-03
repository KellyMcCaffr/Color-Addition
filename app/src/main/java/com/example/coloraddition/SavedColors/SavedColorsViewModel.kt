package com.example.coloraddition.SavedColors

import com.example.coloraddition.SavedColors.SavedColor
import com.example.coloraddition.SavedColors.SavedColorsAction
import com.example.coloraddition.SavedColors.SavedColorsDatabase
import com.example.coloraddition.SavedColors.SavedColorsIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedColorsViewModel(private val db: SavedColorsDatabase) {
    private lateinit var callbackToSavedColors: (List<SavedColor>) -> Any
    fun setSavedColorsCallback(callback: (List<SavedColor>) -> Any) {
        callbackToSavedColors = callback
    }

    fun processIntent(intent: SavedColorsIntent) {
        processAction(intentToAction(intent))
    }

    private fun intentToAction(intent: SavedColorsIntent): SavedColorsAction {
        return when (intent) {
            is SavedColorsIntent.LoadAll -> SavedColorsAction.LoadAll
        }
    }

    private fun processAction(action: SavedColorsAction) {
        when (action) {
            is SavedColorsAction.LoadAll ->
                handleLoadSavedColorsAction()
        }
    }

    private fun handleLoadSavedColorsAction() {
        CoroutineScope(Dispatchers.IO).launch {
            val savedColors = db.SavedColorDao().getAll()
            callbackToSavedColors(savedColors)
        }
    }
}