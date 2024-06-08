package com.example.coloraddition.SavedColors

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.coloraddition.Constants.SAVE_CONTAINER_WIDTH_MIN_DIMEN_FRACTIONAL
import com.example.coloraddition.R
import com.example.coloraddition.SavedColorsDatabase
import com.example.coloraddition.ViewModelUtils
import com.example.coloraddition.ViewUtils
import com.example.coloraddition.ui.theme.ColorAdditionTheme
import kotlin.math.abs

class SavedColorsScreen : ComponentActivity() {

    private var isCombineMode = false
    private var selectedCombineColorSum = ""

    companion object {
        // Display intro toast message once per launch if a color is saved
        private var showedMessageForCurrentLaunch = false
    }

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
            ColorAdditionTheme {
                var selectedCombineColorId by rememberSaveable { mutableStateOf("") }
                val cellSize = ViewUtils.getMinScreenDimensionSize(LocalConfiguration.current) /
                    SAVE_CONTAINER_WIDTH_MIN_DIMEN_FRACTIONAL
                var savedColorsList: List<SavedColor> by rememberSaveable {
                    mutableStateOf(
                        listOf()
                    )
                }
                val viewModelCallback = { it: List<SavedColor> ->
                    savedColorsList = it
                } as (List<SavedColor>) -> Any
                savedColorsViewModel.setSavedColorsCallback(viewModelCallback)
                savedColorsViewModel.processIntent(SavedColorsIntent.LoadAll)
                val context = this
                LazyVerticalGrid(GridCells.Adaptive(minSize = 128.dp)) {
                    if (savedColorsList.isNotEmpty()) {
                        if (!showedMessageForCurrentLaunch) {
                            Toast.makeText(
                                context, getString(R.string.save_message_color_intro),
                                Toast.LENGTH_SHORT
                            ).show()
                            showedMessageForCurrentLaunch = true
                        }
                        items(savedColorsList) {
                            val combineSelectCallback = {color: SavedColor ->
                                if (selectedCombineColorSum.isEmpty()) {
                                    selectedCombineColorId = color.id
                                    selectedCombineColorSum = color.sum
                                } else {
                                    val newColorSum = ViewModelUtils.calculateColorSum(
                                        color.sum, selectedCombineColorSum)
                                    val newHex1 = ViewUtils.getFormattedSumString(color.sum, context)
                                    val newHex2 = ViewUtils.getFormattedSumString(selectedCombineColorSum, context)
                                    ViewUtils.openAddColorsScreenWithNewColor(context,
                                        SavedColor(ViewModelUtils.generateUniqueID(),
                                        newHex1, newHex2, newColorSum))
                                }
                            }
                            SavedColorCell(it, cellSize, selectedCombineColorId, combineSelectCallback)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SavedColorCell(
        savedColor: SavedColor,
        size: Dp,
        selectedCombineColorId: String,
        combineSelectCallback: (SavedColor) -> Any
    ) {
        val context = this
        Surface(
            modifier = Modifier
                .size(size)
                .padding(10.dp)
                .alpha(if(savedColor.id == selectedCombineColorId) { 0.6f } else { 1f })
                .fillMaxSize(1f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (isCombineMode) {
                                combineSelectCallback(savedColor)
                            } else {
                                ViewUtils.openAddColorsScreenWithNewColor(context, savedColor)
                            }
                        },
                        onLongPress = {
                            Toast
                                .makeText(
                                    context, ViewUtils.getFormattedSumString(
                                        savedColor.sum, context
                                    ), Toast.LENGTH_SHORT
                                ).show()
                        }
                    )
                }
            ,
            color = Color(android.graphics.Color.parseColor("#${savedColor.sum}")),
        ) {}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_saved_colors_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.savedColorsAdd -> onBackPressedDispatcher.onBackPressed()
            R.id.savedColorsCombine -> toggleCombineMode()
            R.id.savedColorsClearAll -> savedColorsViewModel.processIntent(SavedColorsIntent.DeleteAll)
        }
        return true
    }

    private fun toggleCombineMode() {
        isCombineMode = !isCombineMode
        if (isCombineMode) {
            Toast.makeText(this, getString(R.string.save_message_combine_intro),
                Toast.LENGTH_SHORT).show()
        }
    }

    // DETECT LEFT-RIGHT SWIPE
    // This code was copied from StackOverflow and converted to Kotlin
    // https://stackoverflow.com/questions/6645537/how-to-detect-the-swipe-left-or-right-in-android
    private var x1 = 0f
    var x2 = 0f
    val MIN_DISTANCE: Int = 150
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> x1 = event.x
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                val deltaX: Float = x2 - x1
                if (abs(deltaX.toDouble()) > MIN_DISTANCE) {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        return super.onTouchEvent(event)
    }
}