package com.on.staccato.presentation.invitation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.on.staccato.presentation.component.topbar.DefaultNavigationTopBar
import com.on.staccato.presentation.invitation.component.InvitationManagementTopBar
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuUiModel
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuUiModel.RECEIVED_INVITATION
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuUiModel.SENT_INVITATION
import com.on.staccato.presentation.invitation.viewmodel.InvitationViewModel
import com.on.staccato.theme.White

@Composable
fun InvitationManagementScreen(
    modifier: Modifier = Modifier,
    invitationViewModel: InvitationViewModel = viewModel(),
    onNavigationClick: () -> Unit,
    defaultSelectedMenu: InvitationSelectionMenuUiModel = RECEIVED_INVITATION,
) {
    var selectedMenu by remember { mutableStateOf(defaultSelectedMenu) }
    val receivedInvitations by invitationViewModel.receivedInvitations.collectAsStateWithLifecycle()
    val sentInvitations by invitationViewModel.sentInvitations.collectAsStateWithLifecycle()
    val dialogState by invitationViewModel.dialogState

    Scaffold(
        containerColor = White,
        topBar = { InvitationManagementTopBar(onNavigationClick) },
    ) { contentPadding ->
        InvitationManagement(
            modifier = modifier.padding(contentPadding),
            selectedMenu = selectedMenu,
            onMenuClick = {
                selectedMenu = it
                when (it) {
                    RECEIVED_INVITATION -> invitationViewModel.getReceivedInvitations()
                    SENT_INVITATION -> invitationViewModel.getSentInvitations()
                }
            },
            receivedInvitations = receivedInvitations,
            onRejectClick = { invitationViewModel.showRejectDialog(it) },
            onAcceptClick = { invitationViewModel.acceptInvitation(it) },
            sentInvitations = sentInvitations,
            onCancelClick = { invitationViewModel.showCancelDialog(it) },
        )

        InvitationDialogs(
            state = dialogState,
            onDismiss = { invitationViewModel.dismissDialog() },
        )
    }
}
