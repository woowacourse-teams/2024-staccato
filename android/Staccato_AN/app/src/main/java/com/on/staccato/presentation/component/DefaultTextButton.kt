package com.on.staccato.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.Gray2
import com.on.staccato.theme.Gray4
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.White

@Composable
fun DefaultTextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    backgroundColor: Color,
    textColor: Color,
    border: BorderStroke? = null,
    textStyle: TextStyle = Body4,
) {
    Box(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickableWithoutRipple { onClick() }
            .border(
                border = border ?:
                BorderStroke(
                    width = 0.dp,
                    color = Color.Transparent,
                ),
                shape = RoundedCornerShape(5.dp),
            )
            .background(
                color = if(enabled) backgroundColor else Gray1,
                shape = RoundedCornerShape(5.dp),
            )
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            )
    ) {
        Text(
            text = text,
            style = textStyle,
            color = if(enabled) textColor else Gray4
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultTextButtonPreview() {
    Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DefaultTextButton(
            text = "활성화 버튼",
            onClick = { },
            backgroundColor = StaccatoBlue,
            textColor = White
        )

        DefaultTextButton(
            text = "비활성화 버튼",
            onClick = { },
            enabled = false,
            backgroundColor = StaccatoBlue,
            textColor = White
        )

        DefaultTextButton(
            text = "거절",
            onClick = { },
            enabled = true,
            backgroundColor = White,
            textColor = StaccatoBlack,
            border = BorderStroke(0.5.dp, Gray2)
        )
    }
}
