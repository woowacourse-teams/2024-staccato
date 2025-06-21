package com.on.staccato.presentation.category.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultTextButtonPadding
import com.on.staccato.presentation.component.DefaultAlertDialog
import com.on.staccato.presentation.component.DefaultTextButton
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.Gray4
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.White

@Composable
fun ExitDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    DefaultAlertDialog(
        title = stringResource(id = R.string.category_exit_title),
        onDismissRequest = onDismiss,
        confirmButton = {
            DefaultTextButton(
                text = stringResource(id = R.string.all_confirm),
                onClick = onConfirm,
                backgroundColor = StaccatoBlue,
                textColor = White,
                contentPadding = DefaultTextButtonPadding,
            )
        },
        dismissButton = {
            DefaultTextButton(
                text = stringResource(id = R.string.all_cancel),
                onClick = onDismiss,
                backgroundColor = Gray1,
                textColor = Gray4,
                contentPadding = DefaultTextButtonPadding,
            )
        },
    )
}

@Preview
@Composable
fun ExitDialogPreview() {
    ExitDialog(
        onDismiss = {},
        onConfirm = {},
    )
}
