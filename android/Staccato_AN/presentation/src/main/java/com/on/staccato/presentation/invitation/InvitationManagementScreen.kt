package com.on.staccato.presentation.invitation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.invitation.component.InvitationDialogs
import com.on.staccato.presentation.invitation.component.InvitationManagement
import com.on.staccato.presentation.invitation.component.InvitationManagementTopBar
import com.on.staccato.presentation.invitation.model.InvitationTabMenu
import com.on.staccato.presentation.invitation.model.InvitationTabMenu.RECEIVED_INVITATION
import com.on.staccato.presentation.invitation.model.InvitationTabMenu.SENT_INVITATION
import com.on.staccato.presentation.invitation.model.ToastMessage
import com.on.staccato.presentation.invitation.viewmodel.InvitationViewModel
import com.on.staccato.presentation.util.showToast
import com.on.staccato.theme.White

@Composable
fun InvitationManagementScreen(
    modifier: Modifier = Modifier,
    invitationViewModel: InvitationViewModel = hiltViewModel(),
    onNavigationClick: () -> Unit,
    defaultSelectedMenu: InvitationTabMenu = RECEIVED_INVITATION,
) {
    val receivedInvitations by invitationViewModel.receivedInvitations.collectAsStateWithLifecycle()
    val sentInvitations by invitationViewModel.sentInvitations.collectAsStateWithLifecycle()
    val dialogState by invitationViewModel.dialogState
    val context = LocalContext.current
    var selectedMenu by remember { mutableStateOf(defaultSelectedMenu) }

    LaunchedEffect(Unit) {
        invitationViewModel.messageEvent.collect { event ->
            val message =
                when (event) {
                    is MessageEvent.FromResource -> context.getString(event.messageId)
                    is MessageEvent.Plain -> event.message
                }
            context.showToast(message)
        }
    }

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
                    RECEIVED_INVITATION -> invitationViewModel.fetchReceivedInvitations()
                    SENT_INVITATION -> invitationViewModel.fetchSentInvitations()
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
