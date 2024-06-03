package com.example.coloraddition.SavedColors

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.coloraddition.SavedColors.SavedColor

@Dao
interface SavedColorDao {
    @Query("SELECT * FROM savedcolor")
    fun getAll(): List<SavedColor>

    @Insert
    fun insertAll(vararg users: SavedColor)

    @Delete
    fun delete(user: SavedColor)
}