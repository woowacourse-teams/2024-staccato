package com.on.staccato.presentation.invitation.sent

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel
import com.on.staccato.presentation.invitation.model.dummySentInvitationUiModels

@Composable
fun SentInvitations(
    sentInvitations: List<SentInvitationUiModel>,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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

@Preview(showBackground = true)
@Composable
private fun SentInvitationPreview() {
    SentInvitations(
        sentInvitations = dummySentInvitationUiModels,
        onCancelClick = {},
    )
}
