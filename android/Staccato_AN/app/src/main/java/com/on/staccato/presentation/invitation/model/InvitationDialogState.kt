package com.on.staccato.presentation.invitation.model

sealed class InvitationDialogState {
    data object None : InvitationDialogState()

    data class Reject(
        val invitationId: Long,
        val onConfirm: () -> Unit,
    ) : InvitationDialogState()

    data class Cancel(
        val invitationId: Long,
        val onConfirm: () -> Unit,
    ) : InvitationDialogState()
}
