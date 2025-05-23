package com.on.staccato.presentation.invitation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.on.staccato.data.invitation.InvitationTempRepository
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.component.topbar.DefaultNavigationTopBar
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuItems
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuItems.RECEIVED_INVITATION
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuItems.SENT_INVITATION
import com.on.staccato.presentation.invitation.received.ReceivedInvitations
import com.on.staccato.presentation.invitation.sent.SentInvitations
import com.on.staccato.presentation.invitation.viewmodel.InvitationViewModel
import com.on.staccato.theme.White

@Composable
fun InvitationManagementScreen(
    modifier: Modifier = Modifier,
    invitationViewModel: InvitationViewModel = viewModel(),
    onNavigationClick: () -> Unit,
    defaultSelectedMenu: InvitationSelectionMenuItems = RECEIVED_INVITATION,
) {
    var selectedMenu by remember { mutableStateOf(defaultSelectedMenu) }
    val receivedInvitations by invitationViewModel.receivedInvitations.collectAsState()
    val sentInvitations by invitationViewModel.sentInvitations.collectAsState()

    Scaffold(
        containerColor = White,
        topBar = { InvitationManagementTopBar(onNavigationClick) },
    ) { contentPadding ->

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
                        receivedInvitations = receivedInvitations,
                        onAcceptClick = {},
                        onRejectClick = {},
                    )
                }

                SENT_INVITATION -> {
                    SentInvitations(
                        sentInvitations = sentInvitations,
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
private fun InvitationManagementScreenPreview(
    @PreviewParameter(InvitationSelectionMenuProvider::class) menu: InvitationSelectionMenuItems,
) {
    InvitationManagementScreen(
        invitationViewModel = InvitationViewModel(InvitationTempRepository()),
        onNavigationClick = {},
        defaultSelectedMenu = menu,
    )
}

class InvitationSelectionMenuProvider : PreviewParameterProvider<InvitationSelectionMenuItems> {
    override val values: Sequence<InvitationSelectionMenuItems>
        get() = sequenceOf(RECEIVED_INVITATION, SENT_INVITATION)
}
