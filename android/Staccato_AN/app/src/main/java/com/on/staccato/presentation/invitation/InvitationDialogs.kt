package com.on.staccato.presentation.invitation

import androidx.compose.runtime.Composable
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

@Composable
fun InvitationDialogs(
    state: InvitationDialogState,
    onDismiss: () -> Unit,
) {
    when (state) {
        is None -> Unit

        is Reject -> {
            DefaultAlertDialog(
                title = "정말 초대를 거절할까요?",
                description = "한 번 거절하면 되돌릴 수 없어요.\n친구가 실망할지도 몰라요!",
                onDismissRequest = onDismiss,
                confirmButton = {
                    DefaultTextButton(
                        text = "확인",
                        onClick = state.onConfirm,
                        backgroundColor = StaccatoBlue,
                        textColor = White,
                    )
                },
                dismissButton = {
                    DefaultTextButton(
                        text = "취소",
                        onClick = onDismiss,
                        backgroundColor = Gray1,
                        textColor = Gray4,
                    )
                },
            )
        }

        is Cancel -> {
            DefaultAlertDialog(
                title = "정말 초대를 취소할까요?",
                description = "취소하더라도 나중에 다시 초대할 수 있어요.",
                onDismissRequest = onDismiss,
                confirmButton = {
                    DefaultTextButton(
                        text = "확인",
                        onClick = state.onConfirm,
                        backgroundColor = StaccatoBlue,
                        textColor = White,
                    )
                },
                dismissButton = {
                    DefaultTextButton(
                        text = "취소",
                        onClick = onDismiss,
                        backgroundColor = Gray1,
                        textColor = Gray4,
                    )
                },
            )
        }
    }
}
