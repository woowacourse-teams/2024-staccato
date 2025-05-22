package com.on.staccato.presentation.invitation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.on.staccato.theme.Body4
import com.on.staccato.theme.StaccatoBlack

@Composable
fun NicknameText(
    nickname: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Body4,
    color: Color = StaccatoBlack,
) {
    Text(
        text = nickname,
        modifier = modifier,
        style = style,
        color = color,
        fontWeight = FontWeight.SemiBold,
    )
}
