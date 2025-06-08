package com.on.staccato.presentation.invitation.received.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.on.staccato.R
import com.on.staccato.presentation.invitation.component.CategoryTitle
import com.on.staccato.presentation.invitation.component.NicknameText
import com.on.staccato.presentation.invitation.component.ProfileImage
import com.on.staccato.presentation.invitation.model.ReceivedInvitationUiModel
import com.on.staccato.presentation.invitation.model.dummyReceivedInvitationLongTitle
import com.on.staccato.presentation.invitation.model.dummyReceivedInvitationUiModels
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray5
import com.on.staccato.theme.White

@Composable
fun ReceivedInvitationItem(
    modifier: Modifier = Modifier,
    categoryInvitation: ReceivedInvitationUiModel,
    onRejectClick: (invitationId: Long) -> Unit,
    onAcceptClick: (invitationId: Long) -> Unit,
) {
    ConstraintLayout(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = White,
                ),
    ) {
        val (profileImage, inviterNickname, guideText, categoryTitle, rejectButton, acceptButton) = createRefs()

        ProfileImage(
            modifier =
                modifier
                    .size(16.dp)
                    .constrainAs(profileImage) {
                        start.linkTo(parent.start, margin = 22.dp)
                        centerVerticallyTo(inviterNickname)
                    },
            url = categoryInvitation.inviterProfileImageUrl,
        )

        NicknameText(
            nickname = categoryInvitation.inviterNickname,
            modifier =
                modifier.constrainAs(inviterNickname) {
                    start.linkTo(profileImage.end, margin = 4.dp)
                    top.linkTo(parent.top, margin = 16.dp)
                },
        )

        Text(
            text = stringResource(id = R.string.invitation_management_guide_text_nickname),
            modifier =
                modifier.constrainAs(guideText) {
                    start.linkTo(inviterNickname.end)
                    centerVerticallyTo(inviterNickname)
                    width = Dimension.wrapContent
                },
            style = Body4,
            color = Gray5,
        )

        CategoryTitle(
            modifier =
                modifier.constrainAs(categoryTitle) {
                    top.linkTo(profileImage.bottom, margin = 8.dp)
                    start.linkTo(profileImage.start)
                    end.linkTo(parent.end, margin = 32.dp)
                    width = Dimension.fillToConstraints
                },
            title = categoryInvitation.categoryTitle,
        )

        RejectButton(
            modifier =
                modifier.constrainAs(rejectButton) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    end.linkTo(acceptButton.start, margin = 4.dp)
                },
            onClick = { onRejectClick(categoryInvitation.invitationId) },
        )

        AcceptButton(
            modifier =
                modifier.constrainAs(acceptButton) {
                    top.linkTo(categoryTitle.bottom, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    end.linkTo(parent.end, margin = 22.dp)
                },
            onClick = { onAcceptClick(categoryInvitation.invitationId) },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0L)
@Composable
private fun ReceivedInvitationItemPreview(
    @PreviewParameter(
        provider = CategoryPreviewProvider::class,
    ) categoryInvitation: ReceivedInvitationUiModel,
) {
    ReceivedInvitationItem(
        categoryInvitation = categoryInvitation,
        onRejectClick = {},
        onAcceptClick = {},
    )
}

private class CategoryPreviewProvider : PreviewParameterProvider<ReceivedInvitationUiModel> {
    override val values: Sequence<ReceivedInvitationUiModel>
        get() = dummyReceivedInvitationUiModels.asSequence()
}

@Preview(name = "제목이 긴 경우", showBackground = true, backgroundColor = 0L)
@Composable
private fun LongTitleItemPreview() {
    ReceivedInvitationItem(
        categoryInvitation = dummyReceivedInvitationLongTitle,
        onRejectClick = {},
        onAcceptClick = {},
    )
}
