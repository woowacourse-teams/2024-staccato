package com.on.staccato.presentation.invitation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.Title3

@Composable
fun CategoryTitle(
    title: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Title3,
    color: Color = StaccatoBlack,
) {
    Text(
        text = title,
        modifier = modifier,
        style = style,
        color = color,
        fontWeight = FontWeight.SemiBold,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}
