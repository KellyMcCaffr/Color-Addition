package com.example.coloraddition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.coloraddition.ui.theme.ColorAdditionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ColorAdditionTheme {
                val widthDp = LocalConfiguration.current.screenWidthDp.dp
                val heightDp = LocalConfiguration.current.screenHeightDp.dp
                val minDimen = if (widthDp >= heightDp) {
                    heightDp
                } else {
                    widthDp
                }
                val imageSize = minDimen / 1.5f
                val containerWidth = minDimen / 2
                val containerPadding = containerWidth / 10
                SumImageView(imageSize, containerPadding, 0.8f)
                SelectionEditTexts(containerWidth, containerPadding)
            }
        }
    }

    @Composable
    fun SumImageView(
        containerSize: Dp,
        containerPadding: Dp,
        heightFraction: Float
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(heightFraction)
                .padding(containerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            // Note: must include '0XFF' for all colors
            val color = Color(0xFF991991)
            Surface(
                modifier = Modifier
                    .border(1.dp, Color.Black, CircleShape)
                    .size(containerSize),
                color = color,
                shape = CircleShape
            ) {

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SelectionEditTexts(
        containerWidth: Dp,
        containerPadding: Dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            val hint = stringResource(R.string.color_selection_hint)
            val maxCharHex = 6
            var color1 by rememberSaveable { mutableStateOf("") }
            var color2 by rememberSaveable { mutableStateOf("") }
            val labelsList = listOf(R.string.color_1_edit_text_label, R.string.color_2_edit_text_label)
            val allowedCharacters = getString(R.string.add_color_characters_allowed)
            for ((c, color) in listOf(color1, color2).withIndex()) {
                val label = labelsList[c]
                OutlinedTextField(
                    value = color,
                    onValueChange = { itOuter ->
                        val condition = itOuter.isEmpty() || (itOuter.none { !allowedCharacters.contains(it) }
                            && itOuter.length <= maxCharHex)
                        if (condition) {
                            if (c == 0) {
                                color1 = itOuter
                            } else {
                                color2 = itOuter
                            }
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
                    label = { Text(stringResource(label)) },
                )
            }
        }
    }
}