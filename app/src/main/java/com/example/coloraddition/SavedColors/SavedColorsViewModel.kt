package com.example.coloraddition.SavedColors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coloraddition.SavedColorsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedColorsViewModel(private val db: SavedColorsDatabase): ViewModel() {
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
            is SavedColorsIntent.DeleteAll -> SavedColorsAction.DeleteAll
        }
    }

    private fun processAction(action: SavedColorsAction) {
        when (action) {
            is SavedColorsAction.LoadAll ->
                handleLoadSavedColorsAction()
            is SavedColorsAction.DeleteAll ->
                handleDeleteSavedColorsAction()
        }
    }

    private fun handleLoadSavedColorsAction() {
        CoroutineScope(Dispatchers.IO).launch {
            val savedColors = db.SavedColorDao().getAll()
            viewModelScope.launch {
                callbackToSavedColors(savedColors)
            }
        }
    }

    private fun handleDeleteSavedColorsAction() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = db.SavedColorDao()
            dao.deleteAll()
            viewModelScope.launch {
                callbackToSavedColors(listOf())
            }
        }
    }
}