package com.example.coloraddition.SavedColors

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coloraddition.SavedColors.SavedColor
import com.example.coloraddition.SavedColors.SavedColorDao

@Database(entities = [SavedColor::class], version = 1)
abstract class SavedColorsDatabase : RoomDatabase() {
    abstract fun SavedColorDao(): SavedColorDao
}