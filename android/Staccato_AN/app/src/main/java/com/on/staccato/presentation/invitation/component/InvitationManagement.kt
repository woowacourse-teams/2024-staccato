package com.on.staccato.presentation.invitation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.invitation.menu.InvitationSelectionMenu
import com.on.staccato.presentation.invitation.model.InvitationTabMenu
import com.on.staccato.presentation.invitation.model.InvitationTabMenu.RECEIVED_INVITATION
import com.on.staccato.presentation.invitation.model.InvitationTabMenu.SENT_INVITATION
import com.on.staccato.presentation.invitation.model.ReceivedInvitationUiModel
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel
import com.on.staccato.presentation.invitation.model.dummyReceivedInvitationUiModels
import com.on.staccato.presentation.invitation.model.dummySentInvitationUiModels
import com.on.staccato.presentation.invitation.received.ReceivedInvitations
import com.on.staccato.presentation.invitation.sent.SentInvitations

@Composable
fun InvitationManagement(
    modifier: Modifier = Modifier,
    selectedMenu: InvitationTabMenu,
    onMenuClick: (InvitationTabMenu) -> Unit,
    receivedInvitations: List<ReceivedInvitationUiModel>,
    onRejectClick: (invitationId: Long) -> Unit,
    onAcceptClick: (invitationId: Long) -> Unit,
    sentInvitations: List<SentInvitationUiModel>,
    onCancelClick: (invitationId: Long) -> Unit,
) {
    Column(modifier = modifier) {
        DefaultDivider()

        InvitationSelectionMenu(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            selectedMenu = selectedMenu,
            onClick = onMenuClick,
        )

        when (selectedMenu) {
            RECEIVED_INVITATION -> {
                ReceivedInvitations(
                    receivedInvitations = receivedInvitations,
                    onRejectClick = onRejectClick,
                    onAcceptClick = onAcceptClick,
                )
            }

            SENT_INVITATION -> {
                SentInvitations(
                    sentInvitations = sentInvitations,
                    onCancelClick = onCancelClick,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun InvitationManagementPreview(
    @PreviewParameter(InvitationSelectionMenuProvider::class) menu: InvitationTabMenu,
) {
    InvitationManagement(
        selectedMenu = menu,
        onMenuClick = { },
        receivedInvitations = dummyReceivedInvitationUiModels,
        onRejectClick = { },
        onAcceptClick = { },
        sentInvitations = dummySentInvitationUiModels,
        onCancelClick = { },
    )
}

private class InvitationSelectionMenuProvider(
    override val values: Sequence<InvitationTabMenu> =
        sequenceOf(RECEIVED_INVITATION, SENT_INVITATION),
) : PreviewParameterProvider<InvitationTabMenu>
