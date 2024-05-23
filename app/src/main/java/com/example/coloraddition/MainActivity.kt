package com.example.coloraddition

import android.graphics.Color.parseColor
import android.os.Bundle
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
import com.example.coloraddition.Constants.COLOR_HEX_ALLOWED_CHARACTERS
import com.example.coloraddition.Constants.DEFAULT_COLOR_SUM
import com.example.coloraddition.Constants.EXPECTED_COLOR_HEX_LENGTH
import com.example.coloraddition.ui.theme.ColorAdditionTheme

class MainActivity : ComponentActivity() {

    private val colorViewModel: ColorViewModel = ColorViewModel("", "",
        DEFAULT_COLOR_SUM, COLOR_HEX_ALLOWED_CHARACTERS)

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
            val sumWidth = minDimen / 2
            val containerWidth = minDimen / 3
            val containerPadding = containerWidth / 10
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
                        colorHex1 = colorText
                    } else if (position == 1) {
                        colorHex2 = colorText
                    }
                    sumString = colorSumString
                } else if (position == -1){
                    Toast.makeText(context, getString(R.string.error_message_non_hex_input),
                        Toast.LENGTH_SHORT).show()
                } else if (position == -2){
                    Toast.makeText(context, getString(R.string.error_message_too_large),
                        Toast.LENGTH_SHORT).show()
                }
            }  as (Int, String, String) -> Any
            colorViewModel.setCallback(viewModelCallback)
            ColorAdditionTheme {
                if (!isLandscapeMode) {
                    setPortraitLayout(
                       containerWidth, sumWidth, containerPadding, sumString,
                       sumStringFormatted, colorSelectionHint, colorSelectionLabelsList,
                       colorHex1, colorHex2, colorViewModel
                    )
                } else {
                    setLandscapeLayout(
                        containerWidth, sumWidth, containerPadding, sumString,
                        sumStringFormatted, colorSelectionHint, colorSelectionLabelsList,
                        colorHex1, colorHex2, colorViewModel
                    )
                }
            }
        }
    }

    @Composable
    fun setPortraitLayout(
        colorSelectionViewWidth: Dp,
        sumViewWidth: Dp,
        containerPadding: Dp,
        sumString: String,
        sumStringFormatted: String,
        hint: String,
        colorSelectionLabelsList: List<String>,
        colorHex1: String,
        colorHex2: String,
        viewModel: ColorViewModel
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f)
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
                    viewModel.processIntent(ColorIntent.ChangeField(position,
                        colorText), DEFAULT_COLOR_SUM)
                },
                viewModel
            )
        }
    }

    @Composable
    fun setLandscapeLayout(
        colorSelectionViewWidth: Dp,
        sumViewWidth: Dp,
        containerPadding: Dp,
        sumString: String,
        sumStringFormatted: String,
        hint: String,
        colorSelectionLabelsList: List<String>,
        colorHex1: String,
        colorHex2: String,
        viewModel: ColorViewModel
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
                    viewModel.processIntent(ColorIntent.ChangeField(position,
                        colorText), DEFAULT_COLOR_SUM)
                },
                viewModel
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
            horizontalArrangement = if (!isLandscape) {Arrangement.Center} else { Arrangement.End },
        ) {
            Surface(
                modifier = Modifier
                    .size(containerWidth),
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
                            fontSize = 20.sp
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
        callback: (Int, String) -> Unit,
        viewModel: ColorViewModel
    ) {
        if (!isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                generateTextViews(
                    containerWidth, containerPadding, hint, color1, color2,
                    labelsList, callback, viewModel
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalAlignment = Alignment.Top
            ) {
                generateTextViews(
                    containerWidth, containerPadding, hint, color1, color2,
                    labelsList, callback, viewModel
                )
            }
        }
    }

    @Composable
    fun generateTextViews(
        containerWidth: Dp,
        containerPadding: Dp,
        hint: String,
        color1: String,
        color2: String,
        labelsList: List<String>,
        callback: (Int, String) -> Unit,
        viewModel: ColorViewModel
    ) {
        for ((c, color) in listOf(color1, color2).withIndex()) {
            val label = labelsList[c]
            ColorSelectionEditText(c, label, colorOuter = color, hint = hint,
                containerWidth = containerWidth, containerPadding = containerPadding, onValueChange =
                {
                    callback(c, it)
                },
                viewModel)
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
        onValueChange: (String) -> Unit,
        viewModel: ColorViewModel
    ) {
        val color = rememberSaveable{ mutableStateOf(colorOuter) }
        OutlinedTextField(
            value = color.value,
            onValueChange = {
               onValueChange(it)
                if (position == 0) {
                    color.value = viewModel.getColor1()
                } else {
                    color.value = viewModel.getColor2()
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
            label = { Text(label) },
        )
    }
}