package com.on.staccato.presentation.invitation.received.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultTextButton
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.White

@Composable
fun AcceptButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    DefaultTextButton(
        modifier = modifier,
        text = stringResource(id = R.string.invitation_management_accept),
        onClick = onClick,
        backgroundColor = StaccatoBlue,
        textColor = White,
    )
}

@Preview(showBackground = true)
@Composable
private fun AcceptButtonPreview() {
    Box(modifier = Modifier.padding(10.dp)) {
        AcceptButton(onClick = {})
    }
}
