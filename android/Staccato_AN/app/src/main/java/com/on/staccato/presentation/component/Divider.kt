package com.on.staccato.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.staccato.theme.Gray2

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    thickness: Dp = 0.5.dp,
    color: Color = Gray2,
) {
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        thickness = thickness,
        color = color,
    )
}
