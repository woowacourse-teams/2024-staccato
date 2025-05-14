package com.on.staccato.presentation.category.invite.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.on.staccato.R
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.Members
import com.on.staccato.domain.model.dummyMembers
import com.on.staccato.domain.model.emptyMembers
import com.on.staccato.presentation.category.invite.model.MembersUiModel
import com.on.staccato.presentation.category.invite.model.MembersUiModel.Companion.emptyMembersUiModel
import com.on.staccato.presentation.category.invite.model.dummyMembersUiModel
import com.on.staccato.theme.White

@Composable
fun InviteDialog(
    searchKeyword: String,
    selectedMembers: Members,
    searchedMembers: MembersUiModel,
    onValueChanged: (String) -> Unit,
    onInviteConfirmed: (Long) -> Unit,
    onClose: () -> Unit,
    onMemberSelected: (Member) -> Unit,
    onMemberDeselected: (Member) -> Unit,
) {
    Dialog(
        onDismissRequest = { onClose() },
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = false,
                decorFitsSystemWindows = false,
            ),
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = White,
            tonalElevation = 10.dp,
            modifier =
                Modifier
                    .fillMaxWidth(0.9f)
                    .height(500.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                InviteTopBar(
                    onDismissRequest = onClose,
                )
                StaccatoSearchTextField(
                    value = searchKeyword,
                    placeholderText = stringResource(R.string.invite_member_search_nickname),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onValueChange = onValueChanged,
                )
                Spacer(modifier = Modifier.height(16.dp))
                SelectedMembersLazyRow(
                    selectedMembers.members,
                    onDeselect = onMemberDeselected,
                )
                SearchedMembersLazyColumn(
                    searchedMembers.members,
                    onSelect = onMemberSelected,
                    onDeselect = onMemberDeselected,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyInviteDialogPreview() {
    InviteDialog(
        searchKeyword = "",
        selectedMembers = emptyMembers,
        searchedMembers = emptyMembersUiModel,
        onValueChanged = {},
        onInviteConfirmed = {},
        onClose = {},
        onMemberDeselected = {},
        onMemberSelected = {},
    )
}

@Preview(showBackground = true)
@Composable
fun EmptySelectedMemberPreview() {
    InviteDialog(
        searchKeyword = "",
        selectedMembers = emptyMembers,
        searchedMembers = dummyMembersUiModel,
        onValueChanged = {},
        onInviteConfirmed = {},
        onClose = {},
        onMemberDeselected = {},
        onMemberSelected = {},
    )
}

@Preview(showBackground = true)
@Composable
fun EmptySearchedMemberPreview() {
    InviteDialog(
        searchKeyword = "",
        selectedMembers = dummyMembers,
        searchedMembers = emptyMembersUiModel,
        onValueChanged = {},
        onInviteConfirmed = {},
        onClose = {},
        onMemberDeselected = {},
        onMemberSelected = {},
    )
}

@Preview(showBackground = true)
@Composable
fun InviteDialogPreview() {
    InviteDialog(
        searchKeyword = "",
        selectedMembers = dummyMembers,
        searchedMembers = dummyMembersUiModel,
        onValueChanged = {},
        onInviteConfirmed = {},
        onClose = {},
        onMemberDeselected = {},
        onMemberSelected = {},
    )
}
