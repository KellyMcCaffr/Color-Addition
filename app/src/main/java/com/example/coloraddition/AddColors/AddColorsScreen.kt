package com.example.coloraddition.AddColors

import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.coloraddition.Constants.ADD_RESULT_CODE_SAVE_INCOMPLETE
import com.example.coloraddition.Constants.ADD_RESULT_CODE_SAVE_SUCCESS
import com.example.coloraddition.Constants.COLOR_HEX_ALLOWED_CHARACTERS
import com.example.coloraddition.Constants.CONTAINER_PADDING_WIDTH_FRACTIONAL
import com.example.coloraddition.Constants.CONTAINER_WIDTH_MIN_DIMEN_FRACTIONAL
import com.example.coloraddition.Constants.DEFAULT_COLOR_SUM
import com.example.coloraddition.Constants.ERROR_CODE_INVALID_INPUT
import com.example.coloraddition.Constants.EXPECTED_COLOR_HEX_LENGTH
import com.example.coloraddition.Constants.SUM_WIDTH_MIN_DIMEN_FRACTIONAL
import com.example.coloraddition.R
import com.example.coloraddition.SavedColors.SavedColorsDatabase
import com.example.coloraddition.SavedColors.SavedColorsScreen
import com.example.coloraddition.ViewUtils
import com.example.coloraddition.ui.theme.ColorAdditionTheme

class AddColorsScreen : ComponentActivity() {

    private val sumViewTextSize = 20.sp

    private val addColorsViewModel by lazy {
        val db = Room.databaseBuilder(
            applicationContext,
            SavedColorsDatabase::class.java, "SavedColorsDatabase"
        ).build()
        AddColorsViewModel(
            "", "",
            DEFAULT_COLOR_SUM, COLOR_HEX_ALLOWED_CHARACTERS,
            db
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        setContent {
            val widthDp = LocalConfiguration.current.screenWidthDp.dp
            val heightDp = LocalConfiguration.current.screenHeightDp.dp
            val minDimen = if (widthDp >= heightDp) {
                heightDp
            } else {
                widthDp
            }
            val sumWidth = minDimen / SUM_WIDTH_MIN_DIMEN_FRACTIONAL
            val containerWidth = minDimen / CONTAINER_WIDTH_MIN_DIMEN_FRACTIONAL
            val containerPadding = containerWidth / CONTAINER_PADDING_WIDTH_FRACTIONAL
            val isLandscapeMode = ViewUtils.getScreenIsLandscapeMode(this)
            val colorSelectionHint = stringResource(R.string.color_selection_hint)
            val colorSelectionLabelsList = listOf(getString(R.string.color_1_edit_text_label),
                getString(R.string.color_2_edit_text_label))
            var colorHex1 by rememberSaveable { mutableStateOf("") }
            var colorHex2 by rememberSaveable { mutableStateOf("") }
            var sumString by rememberSaveable { mutableStateOf(DEFAULT_COLOR_SUM) }
            val sumStringFormatted = if (sumString.length > EXPECTED_COLOR_HEX_LENGTH) {
                sumString.replaceFirst(getString(R.string.hex_string_letter_prefix),"")
            } else {
                sumString
            }
            val viewModelCallback = {position: Int, colorText: String, colorSumString: String ->
                if (position >= 0) {
                    if (position == 0) {
                        colorHex1 = colorText.lowercase()
                    } else if (position == 1) {
                        colorHex2 = colorText.lowercase()
                    }
                    sumString = colorSumString
                } else if (position == ADD_RESULT_CODE_SAVE_SUCCESS){
                    handleSave(context)
                } else {
                    handleError(position, context)
                }
            }  as (Int, String, String) -> Any
            addColorsViewModel.setAddColorsCallback(viewModelCallback)
            ColorAdditionTheme {
                if (!isLandscapeMode) {
                    SetPortraitLayout(
                       containerWidth, sumWidth, containerPadding, sumString,
                       sumStringFormatted, colorSelectionHint, colorSelectionLabelsList,
                       colorHex1, colorHex2
                    )
                } else {
                    SetLandscapeLayout(
                        containerWidth, sumWidth, containerPadding, sumString,
                        sumStringFormatted, colorSelectionHint, colorSelectionLabelsList,
                        colorHex1, colorHex2
                    )
                }
            }
        }
    }

    @Composable
    fun SetPortraitLayout(
        colorSelectionViewWidth: Dp,
        sumViewWidth: Dp,
        containerPadding: Dp,
        sumString: String,
        sumStringFormatted: String,
        hint: String,
        colorSelectionLabelsList: List<String>,
        colorHex1: String,
        colorHex2: String
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(containerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SumView(
                sumViewWidth,
                containerPadding,
                sumString,
                sumStringFormatted,
                false
            )
            ColorSelectionViews(
                containerWidth = colorSelectionViewWidth,
                containerPadding = containerPadding,
                colorHex1,
                colorHex2,
                hint,
                colorSelectionLabelsList,
                false,
                callback = { position, colorText ->
                    addColorsViewModel.processIntent(
                        AddColorsIntent.ChangeField(position, colorText,
                        if (position == 0){ colorHex2 } else { colorHex1 }))
                }
            )
        }
    }

    @Composable
    fun SetLandscapeLayout(
        colorSelectionViewWidth: Dp,
        sumViewWidth: Dp,
        containerPadding: Dp,
        sumString: String,
        sumStringFormatted: String,
        hint: String,
        colorSelectionLabelsList: List<String>,
        colorHex1: String,
        colorHex2: String
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(containerPadding),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top,
        ) {
            ColorSelectionViews(
                containerWidth = colorSelectionViewWidth,
                containerPadding = containerPadding,
                colorHex1,
                colorHex2,
                hint,
                colorSelectionLabelsList,
                true,
                callback = { position, colorText ->
                    addColorsViewModel.processIntent(
                        AddColorsIntent.ChangeField(position,
                        colorText, if (position == 0){ colorHex2 } else { colorHex1 }))
                }
            )
            SumView(
                sumViewWidth,
                containerPadding,
                sumString,
                sumStringFormatted,
                true
            )
        }
    }

    @Composable
    fun SumView(
        containerWidth: Dp,
        containerPadding: Dp,
        sumString: String,
        sumStringFormatted: String,
        isLandscape: Boolean
    ) {
        val modifier = if (isLandscape) {
            Modifier.fillMaxHeight().fillMaxWidth()
        } else {
            Modifier
                .fillMaxWidth()
                .padding(containerPadding)
        }
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (!isLandscape) { Arrangement.Center } else { Arrangement.Start },
        ) {
            Surface(
                modifier = Modifier
                    .size(containerWidth)
                    .padding(containerPadding),
                color = Color(parseColor("#$sumString")),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.background(Color.White), text = sumStringFormatted,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            fontSize = sumViewTextSize
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun ColorSelectionViews(
        containerWidth: Dp,
        containerPadding: Dp,
        color1: String,
        color2: String,
        hint: String,
        labelsList: List<String>,
        isLandscape: Boolean,
        callback: (Int, String) -> Unit
    ) {
        if (!isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                GenerateTextViews(
                    containerWidth, containerPadding, hint, color1, color2,
                    labelsList, callback
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalAlignment = Alignment.Top
            ) {
                GenerateTextViews(
                    containerWidth, containerPadding, hint, color1, color2,
                    labelsList, callback
                )
            }
        }
    }

    @Composable
    fun GenerateTextViews(
        containerWidth: Dp,
        containerPadding: Dp,
        hint: String,
        color1: String,
        color2: String,
        labelsList: List<String>,
        callback: (Int, String) -> Unit
    ) {
        for ((c, color) in listOf(color1, color2).withIndex()) {
            val label = labelsList[c]
            ColorSelectionEditText(c, label, colorOuter = color, hint = hint,
                containerWidth = containerWidth, containerPadding = containerPadding, onValueChange =
                {
                    callback(c, it)
                })
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ColorSelectionEditText(
        position: Int,
        label: String,
        colorOuter: String,
        hint: String,
        containerWidth: Dp,
        containerPadding: Dp,
        onValueChange: (String) -> Unit
    ) {
        var color = colorOuter
        OutlinedTextField(
            value = color,
            onValueChange = {
               onValueChange(it)
                color = if (position == 0) {
                    addColorsViewModel.getColor1()
                } else {
                    addColorsViewModel.getColor2()
                }
            },
            placeholder = {
                Text(hint)
            },
            modifier = Modifier
                .width(containerWidth)
                .padding(containerPadding),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            maxLines = 1,
            label = { Text(label) }
        )
    }

    private fun handleSave(context: Context) {
        // Save query was executed on a background thread
        runOnUiThread {
            run {
                Toast.makeText(
                    context, getString(R.string.save_message_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleError(code: Int, context: Context) {
        when (code) {
            ERROR_CODE_INVALID_INPUT -> Toast.makeText(context, getString(
                R.string.error_message_non_hex_input
            ), Toast.LENGTH_SHORT).show()
            ADD_RESULT_CODE_SAVE_INCOMPLETE -> Toast.makeText(context, getString(
                R.string.save_message_error_no_sum
            ), Toast.LENGTH_SHORT).show()
            ADD_RESULT_CODE_SAVE_SUCCESS -> Toast.makeText(context, getString(
                R.string.save_message_success
            ), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSaveOptionSelected() {
        addColorsViewModel.processIntent(AddColorsIntent.Save)
    }

    private fun onClearOptionSelected() {
        addColorsViewModel.processIntent(AddColorsIntent.Clear)
    }

    private fun onSavedOptionSelected() {
        val i = Intent(applicationContext, SavedColorsScreen::class.java)
        startActivity(i)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_colors_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addColorsSave -> onSaveOptionSelected()
            R.id.addColorsClear -> onClearOptionSelected()
            R.id.addColorsSaved -> onSavedOptionSelected()
        }
        return true
    }
}