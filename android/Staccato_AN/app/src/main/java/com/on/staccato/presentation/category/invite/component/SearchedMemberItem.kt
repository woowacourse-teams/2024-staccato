package com.on.staccato.presentation.category.invite.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.longNameMember
import com.on.staccato.presentation.category.invite.model.InviteState
import com.on.staccato.presentation.category.invite.model.MemberUiModel
import com.on.staccato.presentation.category.invite.model.dummyMemberUiModel
import com.on.staccato.presentation.category.invite.model.participatingMemberUiModel
import com.on.staccato.presentation.category.invite.model.selectedMemberUiModel
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.theme.Gray2
import com.on.staccato.theme.Title3

@Composable
fun SearchedMemberItem(
    item: MemberUiModel,
    onSelect: (Member) -> Unit,
    onDeselect: (Member) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .height(60.dp)
                .alpha(if (item.inviteState == InviteState.PARTICIPATING) 0.4f else 1f),
    ) {
        Spacer(Modifier.width(16.dp))
        DefaultAsyncImage(
            url = item.member.memberImage,
            bitmapPixelSize = 150,
            placeHolder = R.drawable.icon_member,
            modifier =
                Modifier
                    .size(35.dp)
                    .border(
                        width = 0.2.dp,
                        color = Gray2,
                        shape = CircleShape,
                    )
                    .clip(CircleShape),
            contentDescription = R.string.profile_image_description,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = item.member.nickname,
            style = Title3,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier =
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
        )
        Spacer(modifier = Modifier.width(10.dp))
        InviteStateComposable(
            item = item,
            onSelect = onSelect,
            onDeselect = onDeselect,
        )
        Spacer(Modifier.width(16.dp))
    }
    DefaultDivider()
}

@Composable
@Preview(name = "선택 가능 상태", showBackground = true)
fun UnselectedPreview() {
    SearchedMemberItem(
        item = dummyMemberUiModel,
        onDeselect = {},
        onSelect = {},
    )
}

@Composable
@Preview(name = "이름이 아주 길 때", showBackground = true)
fun LongNameSearchedMemberPreview() {
    SearchedMemberItem(
        item = MemberUiModel(longNameMember),
        onDeselect = {},
        onSelect = {},
    )
}

@Composable
@Preview(name = "선택된 상태", showBackground = true)
fun SelectedPreview() {
    SearchedMemberItem(
        item = selectedMemberUiModel,
        onDeselect = {},
        onSelect = {},
    )
}

@Composable
@Preview(name = "참여중 상태", showBackground = true)
fun ParticipatingPreview() {
    SearchedMemberItem(
        item = participatingMemberUiModel,
        onDeselect = {},
        onSelect = {},
    )
}
