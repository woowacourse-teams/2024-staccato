package com.on.staccato.presentation.timeline.compose

import androidx.annotation.ColorRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.domain.model.Member

@Composable
fun Members(
    members: List<Member>,
    hiddenMembersCount: Int,
    @ColorRes color: Int,
) {
    LazyRow(
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
        members = members,
        hiddenMembersCount = 3,
        color = R.color.gray3,
    )
}

// TODO: 서버 API 변경 후 제거
val members =
    listOf(
        Member(memberId = 1, nickname = "빙티", memberImage = "https://avatars.githubusercontent.com/u/46596035?v=4"),
        Member(memberId = 2, nickname = "해나", memberImage = "https://avatars.githubusercontent.com/u/103019852?v=4"),
        Member(memberId = 3, nickname = "호두"),
    )
