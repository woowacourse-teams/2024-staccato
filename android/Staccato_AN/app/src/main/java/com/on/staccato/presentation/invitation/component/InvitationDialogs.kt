package com.on.staccato.presentation.invitation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultAlertDialog
import com.on.staccato.presentation.component.DefaultTextButton
import com.on.staccato.presentation.invitation.model.InvitationDialogState
import com.on.staccato.presentation.invitation.model.InvitationDialogState.Cancel
import com.on.staccato.presentation.invitation.model.InvitationDialogState.None
import com.on.staccato.presentation.invitation.model.InvitationDialogState.Reject
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.Gray4
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.White

private val ButtonPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp)

@Composable
fun InvitationDialogs(
    state: InvitationDialogState,
    onDismiss: () -> Unit,
) {
    when (state) {
        is None -> Unit

        is Reject -> {
            DefaultAlertDialog(
                title = stringResource(id = R.string.invitation_management_dialog_reject_title),
                onDismissRequest = onDismiss,
                confirmButton = {
                    DefaultTextButton(
                        text = stringResource(id = R.string.all_confirm),
                        onClick = state.onConfirm,
                        backgroundColor = StaccatoBlue,
                        textColor = White,
                        contentPadding = ButtonPaddingValues,
                    )
                },
                dismissButton = {
                    DefaultTextButton(
                        text = stringResource(id = R.string.all_cancel),
                        onClick = onDismiss,
                        backgroundColor = Gray1,
                        textColor = Gray4,
                        contentPadding = ButtonPaddingValues,
                    )
                },
            )
        }

        is Cancel -> {
            DefaultAlertDialog(
                title = stringResource(id = R.string.invitation_management_dialog_cancel_title),
                onDismissRequest = onDismiss,
                confirmButton = {
                    DefaultTextButton(
                        text = stringResource(id = R.string.all_confirm),
                        onClick = state.onConfirm,
                        backgroundColor = StaccatoBlue,
                        textColor = White,
                        contentPadding = ButtonPaddingValues,
                    )
                },
                dismissButton = {
                    DefaultTextButton(
                        text = stringResource(id = R.string.all_cancel),
                        onClick = onDismiss,
                        backgroundColor = Gray1,
                        textColor = Gray4,
                        contentPadding = ButtonPaddingValues,
                    )
                },
            )
        }
    }
}
