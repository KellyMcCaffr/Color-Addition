package com.example.coloraddition

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
    fun insertAll(vararg colors: SavedColor)

    @Delete
    fun delete(color: SavedColor)

    fun deleteColor(color: SavedColor) {
        val colors = getAll()
        for (checkColor in colors) {
            if ((checkColor.hex1 == color.hex1 && checkColor.hex2 == color.hex2) ||
                (checkColor.hex2 == color.hex1 && checkColor.hex1 == color.hex2)
            ) {
                // Direct delete does not work because id will differ
                delete(checkColor)
            }
        }
    }

    @Delete
    fun deleteAll() {
        val colors = getAll()
        for (color in colors) {
            delete(color)
        }
    }
}