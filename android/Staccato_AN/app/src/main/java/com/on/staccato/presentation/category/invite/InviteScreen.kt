package com.on.staccato.presentation.category.invite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.category.invite.component.InviteDialog
import com.on.staccato.presentation.category.invite.model.MembersUiModel
import com.on.staccato.presentation.category.viewmodel.CategoryViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun InviteScreen(viewModel: CategoryViewModel = hiltViewModel()) {
    val isInviteModel by viewModel.isInviteMode.collectAsState()
    val selectedMembers by viewModel.selectedMembers.collectAsState()
    val searchedMembers by viewModel.members.collectAsState(MembersUiModel.emptyMembersUiModel)
    var searchKeyword by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        snapshotFlow { searchKeyword }
            .debounce(300L)
            .collectLatest { debouncedText ->
                if (debouncedText.isNotBlank()) {
                    viewModel.searchMembersBy(debouncedText)
                } else {
                    viewModel.clearSearchMembers()
                }
            }
    }

    if (isInviteModel) {
        InviteDialog(
            searchKeyword = searchKeyword,
            selectedMembers = selectedMembers,
            searchedMembers = searchedMembers,
            onValueChanged = { newValue -> searchKeyword = newValue },
            onClose = {
                viewModel.toggleInviteMode(false)
                searchKeyword = ""
            },
            onMemberSelected = { selectedMate -> viewModel.select(selectedMate) },
            onMemberDeselected = { unselectedMate -> viewModel.unselect(unselectedMate) },
            onInviteConfirmed = { mateId -> viewModel.inviteMemberBy(mateId) },
        )
    }
}
