package com.example.coloraddition

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
}