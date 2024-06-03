package com.example.coloraddition.SavedColors

sealed class SavedColorsIntent {
    object LoadAll: SavedColorsIntent()
}