package com.on.staccato.presentation.invitation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.component.topbar.DefaultNavigationTopBar
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuItems.RECEIVED_INVITATION
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuItems.SENT_INVITATION
import com.on.staccato.presentation.invitation.model.dummyInvitationUiModels
import com.on.staccato.presentation.invitation.received.ReceivedInvitations
import com.on.staccato.presentation.invitation.sent.SentInvitations
import com.on.staccato.theme.White

@Composable
fun InvitationManagementScreen(
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = White,
        topBar = { InvitationManagementTopBar(onNavigationClick) },
    ) { contentPadding ->
        var selectedMenu by remember { mutableStateOf(RECEIVED_INVITATION) }

        Column(modifier = modifier.padding(contentPadding)) {
            DefaultDivider()

            InvitationSelectionMenu(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                selectedMenu = selectedMenu,
                onClick = { selectedMenu = it },
            )

            when(selectedMenu) {
                RECEIVED_INVITATION -> {
                    ReceivedInvitations(
                        receivedInvitations = dummyInvitationUiModels,
                        onAcceptClick = {},
                        onRejectClick = {},
                    )
                }

                SENT_INVITATION -> {
                    SentInvitations(
                        sentInvitations = dummyInvitationUiModels,
                        onCancelClick = {},
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvitationManagementTopBar(
    onNavigationClick: () -> Unit,
) {
    DefaultNavigationTopBar(
        title = "카테고리 초대 관리",
        onNavigationClick = onNavigationClick,
    )
}

@Preview(showBackground = true)
@Composable
private fun InvitationManagementScreenPreview() {
    InvitationManagementScreen(onNavigationClick = {})
}
