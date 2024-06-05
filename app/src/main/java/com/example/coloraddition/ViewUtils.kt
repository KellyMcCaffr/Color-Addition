package com.example.coloraddition

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.coloraddition.AddColors.AddColorsScreen
import com.example.coloraddition.SavedColors.SavedColor
import com.example.coloraddition.SavedColors.SavedColorsScreen

object ViewUtils {
    fun getScreenIsLandscapeMode(
        context: Context
    ): Boolean {
        return context.resources.configuration.orientation ==
            Configuration.ORIENTATION_LANDSCAPE
    }

    fun getMinScreenDimensionSize(
        localConfiguration: Configuration
    ): Dp {
        val widthDp = localConfiguration.screenWidthDp.dp
        val heightDp = localConfiguration.screenHeightDp.dp
        return if (widthDp >= heightDp) {
            heightDp
        } else {
            widthDp
        }
    }

    fun openSavedColorsScreen(context: Context) {
        val i = Intent(context, SavedColorsScreen::class.java)
        context.startActivity(i)
    }

    fun openAddColorsScreenWithNewColor(context: Context, savedColor: SavedColor) {
        val i = Intent(context, AddColorsScreen::class.java)
        i.putExtra(context.getString(R.string.extra_color_to_load), savedColor)
        context.startActivity(i)
    }

    fun getFormattedSumString(
        sumString: String,
        context: Context
    ): String {
        return if (sumString.length > Constants.EXPECTED_COLOR_HEX_LENGTH) {
            sumString.replaceFirst(context.getString(R.string.hex_string_letter_prefix),"")
        } else {
            sumString
        }
    }
}