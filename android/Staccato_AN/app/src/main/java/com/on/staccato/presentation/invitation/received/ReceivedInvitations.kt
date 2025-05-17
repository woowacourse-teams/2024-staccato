package com.on.staccato.presentation.invitation.received

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.invitation.model.InvitationUiModel
import com.on.staccato.presentation.invitation.model.dummyInvitationUiModels

@Composable
fun ReceivedInvitations(
    modifier: Modifier = Modifier,
    receivedInvitations: List<InvitationUiModel>,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
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
        receivedInvitations = dummyInvitationUiModels,
        onAcceptClick = {},
        onRejectClick = {},
    )
}
