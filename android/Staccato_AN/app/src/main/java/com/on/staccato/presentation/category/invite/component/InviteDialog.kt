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
    onInviteConfirmed: () -> Unit,
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
                    participantsNumber = selectedMembers.members.size,
                    onInviteConfirmed = onInviteConfirmed,
                )
                SearchTextField(
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

@Preview(
    name = "검색 결과, 선택 모두 비어있을 때",
    showBackground = true,
)
@Composable
private fun EmptyInviteDialogPreview() {
    PreviewInviteDialog(
        selectedMembers = emptyMembers,
        searchedMembers = emptyMembersUiModel,
    )
}

@Preview(
    name = "선택만 비어있을 때",
    showBackground = true,
)
@Composable
private fun EmptySelectedMemberPreview() {
    PreviewInviteDialog(
        selectedMembers = emptyMembers,
        searchedMembers = dummyMembersUiModel,
    )
}

@Preview(
    name = "검색 결과만 비어있을 때",
    showBackground = true,
)
@Composable
private fun EmptySearchedMemberPreview() {
    PreviewInviteDialog(
        selectedMembers = dummyMembers,
        searchedMembers = emptyMembersUiModel,
    )
}

@Preview(
    name = "검색 결과, 선택 모두 존재할 때",
    showBackground = true,
)
@Composable
private fun InviteDialogPreview() {
    PreviewInviteDialog(
        selectedMembers = dummyMembers,
        searchedMembers = dummyMembersUiModel,
    )
}

@Composable
private fun PreviewInviteDialog(
    selectedMembers: Members,
    searchedMembers: MembersUiModel,
) {
    InviteDialog(
        searchKeyword = "",
        selectedMembers = selectedMembers,
        searchedMembers = searchedMembers,
        onValueChanged = {},
        onInviteConfirmed = {},
        onClose = {},
        onMemberDeselected = {},
        onMemberSelected = {},
    )
}
