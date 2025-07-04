package com.on.staccato.presentation.invitation.sent

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.R
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.component.DefaultEmptyView
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel
import com.on.staccato.presentation.invitation.model.dummySentInvitationUiModels
import com.on.staccato.presentation.invitation.sent.component.SentInvitationItem

@Composable
fun SentInvitations(
    sentInvitations: List<SentInvitationUiModel>,
    onCancelClick: (invitationId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (sentInvitations.isEmpty()) {
        DefaultEmptyView(description = stringResource(id = R.string.invitation_management_sent_empty))
    } else {
        LazyColumn(modifier = modifier) {
            itemsIndexed(sentInvitations) { index, invitation ->
                SentInvitationItem(
                    categoryInvitation = invitation,
                    onCancelClick = onCancelClick,
                )
                if (index != sentInvitations.lastIndex) {
                    DefaultDivider()
                }
            }
        }
    }
}

@Preview
@Composable
private fun SentInvitationPreview() {
    SentInvitations(
        sentInvitations = dummySentInvitationUiModels,
        onCancelClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun EmptySentInvitationPreview() {
    SentInvitations(
        sentInvitations = emptyList(),
        onCancelClick = {},
    )
}
