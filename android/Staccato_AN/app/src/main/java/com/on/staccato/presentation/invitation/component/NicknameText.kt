package com.on.staccato.presentation.invitation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3

@Composable
fun NicknameText(
    nickname: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Body4,
    color: Color = Gray3,
) {
    Text(
        text = nickname,
        modifier = modifier,
        style = style,
        color = color,
    )
}
