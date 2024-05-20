package com.example.coloraddition

// Model contains the data represented as an immutable state
data class ColorState(val colorHex1: String, val colorHex2: String, var colorSum: String)