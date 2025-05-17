package com.on.staccato.presentation.timeline.component

import androidx.annotation.ColorRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.common.MemberUiModel
import com.on.staccato.presentation.common.dummyMembersUiModel

@Composable
fun Members(
    modifier: Modifier = Modifier,
    members: List<MemberUiModel>,
    hiddenMembersCount: Int,
    @ColorRes color: Int,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(2.dp),
        horizontalArrangement = Arrangement.spacedBy((-8).dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(members) { member ->
            MemberItem(memberImageUrl = member.memberImage)
        }

        item {
            HiddenMembersCountItem(hiddenMembersCount, color = color)
        }
    }
}

@Preview
@Composable
private fun MembersPreview() {
    Members(
        members = dummyMembersUiModel,
        hiddenMembersCount = 3,
        color = R.color.gray3,
    )
}
