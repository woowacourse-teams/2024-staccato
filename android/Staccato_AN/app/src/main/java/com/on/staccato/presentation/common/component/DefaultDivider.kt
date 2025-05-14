package com.on.staccato.presentation.common.component

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.on.staccato.theme.Gray2

@Composable
fun DefaultDivider() {
    HorizontalDivider(
        thickness = 0.5.dp,
        color = Gray2,
    )
}
