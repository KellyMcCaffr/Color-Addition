package com.example.coloraddition.SavedColors

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.room.Room
import com.example.coloraddition.R
import com.example.coloraddition.ViewUtils
import com.example.coloraddition.ui.theme.ColorAdditionTheme

class SavedColorsScreen : ComponentActivity() {

    private val savedColorsViewModel by lazy {
        val db = Room.databaseBuilder(
            applicationContext,
            SavedColorsDatabase::class.java, "SavedColorsDatabase"
        ).build()
        SavedColorsViewModel(db)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModelCallback = { savedColorsList: List<SavedColor> ->
                Log.e("4553434","Here is saved colors list 34: $savedColorsList")
            } as (List<SavedColor>) -> Any
            val isLandscapeMode = ViewUtils.getScreenIsLandscapeMode(this)
            savedColorsViewModel.setSavedColorsCallback(viewModelCallback)
            savedColorsViewModel.processIntent(SavedColorsIntent.LoadAll)
            ColorAdditionTheme {
                if (!isLandscapeMode) {
                    SetPortraitLayout()
                } else {
                    SetLandscapeLayout()
                }
            }
        }
    }

    @Composable
    fun SetPortraitLayout(

    ) {

    }

    @Composable
    fun SetLandscapeLayout(

    ) {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_saved_colors_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.savedColorsAdd)  {
            onBackPressedDispatcher.onBackPressed()
        }
        return true
    }
}