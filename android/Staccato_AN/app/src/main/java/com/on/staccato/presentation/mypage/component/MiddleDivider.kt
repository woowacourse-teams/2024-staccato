package com.on.staccato.presentation.mypage.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.theme.Gray1

@Composable
fun MiddleDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 10.dp,
    color: Color = Gray1,
) {
    DefaultDivider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
}

@Preview(showBackground = true)
@Composable
fun MiddleDividerPreview() {
    MiddleDivider()
}
