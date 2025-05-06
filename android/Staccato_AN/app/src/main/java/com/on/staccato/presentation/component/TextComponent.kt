package com.on.staccato.presentation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.on.staccato.theme.StaccatoBlack

@Composable
fun TextComponent(
    modifier: Modifier = Modifier,
    color: Color = StaccatoBlack,
    description: String,
    style: TextStyle,
) {
    Text(
        text = description,
        modifier = modifier,
        style = style,
        color = color,
    )
}
