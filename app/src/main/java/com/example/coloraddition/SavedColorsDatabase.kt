package com.example.coloraddition

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coloraddition.SavedColors.SavedColor

@Database(entities = [SavedColor::class], version = 1)
abstract class SavedColorsDatabase : RoomDatabase() {
    abstract fun SavedColorDao(): SavedColorDao
}