package com.example.coloraddition

import android.graphics.Color.parseColor
import android.os.Bundle
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
import com.example.coloraddition.ui.theme.ColorAdditionTheme

class MainActivity : ComponentActivity() {

    private val colorViewModel: ColorViewModel = ColorViewModel("", "",
        DEFAULT_COLOR_SUM, COLOR_HEX_ALLOWED_CHARACTERS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val widthDp = LocalConfiguration.current.screenWidthDp.dp
            val heightDp = LocalConfiguration.current.screenHeightDp.dp
            val minDimen = if (widthDp >= heightDp) {
                heightDp
            } else {
                widthDp
            }
            val containerWidth = minDimen / 2
            val containerPadding = containerWidth / 10
            var colorHex1 by rememberSaveable { mutableStateOf("") }
            var colorHex2 by rememberSaveable { mutableStateOf("") }
            var sumString by rememberSaveable { mutableStateOf(DEFAULT_COLOR_SUM) }
            val sumStringFormatted = sumString.replaceFirst(getString(
                R.string.hex_string_letter_prefix),"")
            ColorAdditionTheme {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(1f)
                        .padding(containerPadding),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val viewModelCallback = {position: Int, colorText: String, colorSumString: String ->
                        if (position == 0) {
                            colorHex1 = colorText
                        } else {
                            colorHex2 = colorText
                        }
                        sumString = colorSumString
                    }
                    colorViewModel.setCallback(viewModelCallback)
                    SumView(
                        containerWidth,
                        containerPadding,
                        sumString,
                        sumStringFormatted
                    )
                    ColorSelectionViews(
                        containerWidth = containerWidth,
                        containerPadding = containerPadding,
                        colorHex1,
                        colorHex2,
                        callback = { position, colorText ->
                            colorViewModel.processIntent(ColorIntent.ChangeField(position,
                                colorText), DEFAULT_COLOR_SUM)
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun SumView(
        containerWidth: Dp,
        containerPadding: Dp,
        sumString: String,
        sumStringFormatted: String
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(containerPadding),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center,
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
        callback: (Int, String) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            val hint = stringResource(R.string.color_selection_hint)
            val labelsList = listOf(getString(R.string.color_1_edit_text_label),
                getString(R.string.color_2_edit_text_label))
            for ((c, color) in listOf(color1, color2).withIndex()) {
                val label = labelsList[c]
                ColorSelectionEditText(c, label, colorOuter = color, hint = hint,
                    containerWidth = containerWidth, containerPadding = containerPadding, onValueChange =
                    {
                        callback(c, it)
                    })
            }
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
        val color = rememberSaveable{ mutableStateOf(colorOuter) }
        OutlinedTextField(
            value = color.value,
            onValueChange = {
               onValueChange(it)
                if (position == 0) {
                    color.value = colorViewModel.getColor1()
                } else {
                    color.value = colorViewModel.getColor2()
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