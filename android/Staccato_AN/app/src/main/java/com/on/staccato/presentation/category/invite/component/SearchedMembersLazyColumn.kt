package com.on.staccato.presentation.category.invite.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.domain.model.Member
import com.on.staccato.presentation.category.invite.model.MemberUiModel
import com.on.staccato.presentation.category.invite.model.MembersUiModel.Companion.emptyMembersUiModel
import com.on.staccato.presentation.category.invite.model.dummyMembersUiModel

@Composable
fun SearchedMembersLazyColumn(
    members: List<MemberUiModel>,
    onSelect: (Member) -> Unit,
    onDeselect: (Member) -> Unit,
) {
    if (members.isEmpty()) {
        SearchEmptyView()
    } else {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            items(members) { item ->
                SearchedMemberItem(
                    item = item,
                    onSelect = onSelect,
                    onDeselect = onDeselect,
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SearchedMembersLazyColumnPreview() {
    SearchedMembersLazyColumn(
        dummyMembersUiModel.members,
        {},
        {},
    )
}

@Composable
@Preview(showBackground = true)
fun EmptySearchedMembersLazyColumnPreview() {
    SearchedMembersLazyColumn(
        emptyMembersUiModel.members,
        {},
        {},
    )
}
