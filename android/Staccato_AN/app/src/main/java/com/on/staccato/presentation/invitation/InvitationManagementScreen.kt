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
import com.on.staccato.presentation.invitation.component.InvitationDialogs
import com.on.staccato.presentation.invitation.component.InvitationManagement
import com.on.staccato.presentation.invitation.component.InvitationManagementTopBar
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuUiModel
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuUiModel.RECEIVED_INVITATION
import com.on.staccato.presentation.invitation.model.InvitationSelectionMenuUiModel.SENT_INVITATION
import com.on.staccato.presentation.invitation.model.ToastMessage
import com.on.staccato.presentation.invitation.viewmodel.InvitationViewModel
import com.on.staccato.presentation.util.showToast
import com.on.staccato.theme.White

@Composable
fun InvitationManagementScreen(
    modifier: Modifier = Modifier,
    invitationViewModel: InvitationViewModel = hiltViewModel(),
    onNavigationClick: () -> Unit,
    defaultSelectedMenu: InvitationSelectionMenuUiModel = RECEIVED_INVITATION,
) {
    val receivedInvitations by invitationViewModel.receivedInvitations.collectAsStateWithLifecycle()
    val sentInvitations by invitationViewModel.sentInvitations.collectAsStateWithLifecycle()
    val dialogState by invitationViewModel.dialogState
    val context = LocalContext.current
    var selectedMenu by remember { mutableStateOf(defaultSelectedMenu) }

    LaunchedEffect(Unit) {
        invitationViewModel.toastMessage.collect {
            val message =
                when (it) {
                    is ToastMessage.FromResource -> context.getString(it.messageId)
                    is ToastMessage.Plain -> it.errorMessage
                }
            context.showToast(message)
        }
    }

    LaunchedEffect(Unit) {
        invitationViewModel.exceptionState.collect { state ->
            context.showToast(context.getString(state.messageId))
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
