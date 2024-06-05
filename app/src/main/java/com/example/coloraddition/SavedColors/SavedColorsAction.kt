package com.example.coloraddition.SavedColors

sealed class SavedColorsAction {
    object LoadAll: SavedColorsAction()

    object DeleteAll: SavedColorsAction()
}