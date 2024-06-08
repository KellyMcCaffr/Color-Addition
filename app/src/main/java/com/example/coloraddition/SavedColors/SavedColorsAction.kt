package com.example.coloraddition.SavedColors

sealed class SavedColorsAction {
    object LoadAll: SavedColorsAction()
    object DeleteAll: SavedColorsAction()
    class CombineLocal(val newCombineColor: SavedColor): SavedColorsAction()
    class OpenLocal(val colorToOpen: SavedColor): SavedColorsAction()

}