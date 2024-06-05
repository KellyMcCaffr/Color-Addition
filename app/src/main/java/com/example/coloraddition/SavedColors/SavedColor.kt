package com.example.coloraddition.SavedColors

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SavedColor (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "hex_1") val hex1: String,
    @ColumnInfo(name = "hex_2") val hex2: String,
    @ColumnInfo(name = "sum") val sum: String
)