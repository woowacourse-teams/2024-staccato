package com.on.staccato.presentation.component

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.staccato.theme.Gray1

@Composable
fun DefaultDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = Gray1,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
}
