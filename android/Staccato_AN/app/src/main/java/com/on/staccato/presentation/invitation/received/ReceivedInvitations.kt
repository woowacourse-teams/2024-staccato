package com.on.staccato.presentation.invitation.received

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.invitation.model.ReceivedInvitationUiModel
import com.on.staccato.presentation.invitation.model.dummyReceivedInvitationUiModels

@Composable
fun ReceivedInvitations(
    modifier: Modifier = Modifier,
    receivedInvitations: List<ReceivedInvitationUiModel>,
    onRejectClick: (invitationId: Long) -> Unit,
    onAcceptClick: (invitationId: Long) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(receivedInvitations) { index, invitation ->
            ReceivedInvitationItem(
                categoryInvitation = invitation,
                onRejectClick = onRejectClick,
                onAcceptClick = onAcceptClick,
            )
            if (index != receivedInvitations.lastIndex) {
                DefaultDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceivedInvitationPreview() {
    ReceivedInvitations(
        receivedInvitations = dummyReceivedInvitationUiModels,
        onAcceptClick = {},
        onRejectClick = {},
    )
}
