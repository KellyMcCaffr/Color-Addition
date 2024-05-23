package com.example.coloraddition

import android.content.Context
import android.content.res.Configuration

object ViewUtils {
    fun getScreenIsLandscapeMode(
        context: Context
    ): Boolean {
        return context.resources.configuration.orientation ==
            Configuration.ORIENTATION_LANDSCAPE
    }
}