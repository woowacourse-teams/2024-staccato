package com.on.staccato.presentation.invitation.sent.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.component.DefaultTextButton
import com.on.staccato.theme.Accents4
import com.on.staccato.theme.White

@Composable
fun CancelButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    DefaultTextButton(
        modifier = modifier,
        text = "취소",
        onClick = onClick,
        backgroundColor = Accents4,
        textColor = White,
    )
}

@Preview(showBackground = true)
@Composable
private fun CancelButtonPreview() {
    Box(modifier = Modifier.padding(10.dp)) {
        CancelButton {}
    }
}
