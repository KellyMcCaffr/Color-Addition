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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coloraddition.Constants.EXPECTED_COLOR_HEX_LENGTH
import com.example.coloraddition.ui.theme.ColorAdditionTheme

class MainActivity : ComponentActivity() {

    private var colorViewModel: ColorViewModel? = null

    @OptIn(ExperimentalMaterial3Api::class)
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
                val containerWidth = minDimen / 2
                val containerPadding = containerWidth / 10
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(1f)
                        .padding(containerPadding),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    var color1 by rememberSaveable { mutableStateOf("") }
                    var color2 by rememberSaveable { mutableStateOf("") }
                    var colorSumResult by rememberSaveable { mutableStateOf(0) }
                    // TODO: move this to ViewModel
                    val sumString = if (color1.length == EXPECTED_COLOR_HEX_LENGTH
                        && color2.length == EXPECTED_COLOR_HEX_LENGTH) {
                        val c1 = parseColor("#$color1")
                        val c2 = parseColor("#$color2")
                        colorSumResult = mixTwoColors(c1, c2, 0.5f)
                        remember{mutableStateOf(Integer.toHexString(colorSumResult))}.value
                    } else {
                        remember{mutableStateOf(getString(R.string.initial_hex_color_value))}.value
                    }
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
                                val sumStringFormatted = sumString.replaceFirst(getString(
                                    R.string.hex_string_letter_prefix),"")
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val hint = stringResource(R.string.color_selection_hint)
                        val labelsList = listOf(R.string.color_1_edit_text_label, R.string.color_2_edit_text_label)
                        val allowedCharacters = getString(R.string.add_color_characters_allowed)
                        for ((c, color) in listOf(color1, color2).withIndex()) {
                            val label = labelsList[c]
                            OutlinedTextField(
                                value = color,
                                onValueChange = { itOuter ->
                                    // Only allow characters present in a default hex
                                    val inputIsValid = itOuter.none { !allowedCharacters.contains(it) } &&
                                            itOuter.length <= EXPECTED_COLOR_HEX_LENGTH
                                    if (inputIsValid) {
                                        if (c == 0) {
                                            color1 = itOuter
                                        } else {
                                            color2 = itOuter
                                        }
                                        val state = ColorState(color1, color2, sumString.toString())
                                        if (colorViewModel == null) {
                                            colorViewModel = ColorViewModel(state)
                                        } else {
                                            colorViewModel?.state = state
                                        }
                                        colorViewModel?.processIntent(ColorIntent.ChangeField(c, itOuter))
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
        }
    }

    // This method was copied from StackOverflow and converted to Kotlin
    // https://stackoverflow.com/questions/6070163/color-mixing-in-android
    private fun mixTwoColors(color1: Int, color2: Int, amount: Float): Int {
        val ALPHA_CHANNEL: Byte = 24
        val RED_CHANNEL: Byte = 16
        val GREEN_CHANNEL: Byte = 8
        val BLUE_CHANNEL: Byte = 0
        val inverseAmount = 1.0f - amount
        val a =
            ((color1 shr ALPHA_CHANNEL.toInt() and 0xff).toFloat() * amount + (color2 shr ALPHA_CHANNEL.toInt() and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val r =
            ((color1 shr RED_CHANNEL.toInt() and 0xff).toFloat() * amount + (color2 shr RED_CHANNEL.toInt() and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val g =
            ((color1 shr GREEN_CHANNEL.toInt() and 0xff).toFloat() * amount + (color2 shr GREEN_CHANNEL.toInt() and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val b =
            ((color1 and 0xff).toFloat() * amount + (color2 and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        return a shl ALPHA_CHANNEL.toInt() or (r shl RED_CHANNEL.toInt()) or (g shl GREEN_CHANNEL.toInt()) or (b shl BLUE_CHANNEL.toInt())
    }
}