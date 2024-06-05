package com.example.coloraddition.SavedColors

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.coloraddition.Constants.SAVE_CONTAINER_WIDTH_MIN_DIMEN_FRACTIONAL
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
            val cellSize = ViewUtils.getMinScreenDimensionSize(LocalConfiguration.current) /
                    SAVE_CONTAINER_WIDTH_MIN_DIMEN_FRACTIONAL
        var savedColorsList: List<SavedColor> by rememberSaveable {
                mutableStateOf(listOf())
            }
            val viewModelCallback = { it: List<SavedColor> ->
                savedColorsList = it
            } as (List<SavedColor>) -> Any
            savedColorsViewModel.setSavedColorsCallback(viewModelCallback)
            savedColorsViewModel.processIntent(SavedColorsIntent.LoadAll)
            ColorAdditionTheme {
                LazyVerticalGrid(GridCells.Adaptive(minSize = 128.dp)) {
                    items(savedColorsList) {
                        SavedColorCell(savedColor = it, size = cellSize)
                    }
                }
            }
        }
    }

    @Composable
    fun SavedColorCell(
        savedColor: SavedColor,
        size: Dp
    ) {
        Surface(
            modifier = Modifier
                .size(size)
                .padding(10.dp)
                .fillMaxSize(1f),
            color = Color(android.graphics.Color.parseColor("#${savedColor.sum}")),
        ) {

        }
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