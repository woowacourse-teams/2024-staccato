package com.on.staccato.presentation.invitation.received.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultTextButton
import com.on.staccato.theme.Gray2
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.White

@Composable
fun RejectButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    DefaultTextButton(
        modifier = modifier,
        text = stringResource(id = R.string.invitation_management_reject),
        onClick = onClick,
        backgroundColor = White,
        textColor = StaccatoBlack,
        border = BorderStroke(width = 0.5.dp, color = Gray2),
    )
}

@Preview(showBackground = true)
@Composable
private fun RejectButtonPreview() {
    Box(modifier = Modifier.padding(10.dp)) {
        RejectButton(onClick = {})
    }
}
