package com.on.staccato.presentation.invitation.received

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.on.staccato.presentation.component.DefaultTextButton
import com.on.staccato.presentation.invitation.component.CategoryTitle
import com.on.staccato.presentation.invitation.component.NicknameText
import com.on.staccato.presentation.invitation.component.ProfileImage
import com.on.staccato.presentation.invitation.model.ReceivedInvitationUiModel
import com.on.staccato.presentation.invitation.model.dummyReceivedInvitationUiModels
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray2
import com.on.staccato.theme.Gray5
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.StaccatoBlue
import com.on.staccato.theme.White

@Composable
fun ReceivedInvitationItem(
    modifier: Modifier = Modifier,
    categoryInvitation: ReceivedInvitationUiModel,
    onRejectClick: (invitationId: Long) -> Unit,
    onAcceptClick: (invitationId: Long) -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = White,
            ),
    ) {
        val (profileImage, inviterNickname, guideText, categoryTitle, rejectButton, acceptButton) = createRefs()

        ProfileImage(
            modifier = modifier
                .size(16.dp)
                .constrainAs(profileImage) {
                    start.linkTo(parent.start, margin = 22.dp)
                    centerVerticallyTo(inviterNickname)
                },
            url = categoryInvitation.inviterProfileImageUrl,
        )

        NicknameText(
            nickname = categoryInvitation.inviterNickname,
            modifier = modifier.constrainAs(inviterNickname) {
                start.linkTo(profileImage.end, margin = 4.dp)
                top.linkTo(parent.top, margin = 16.dp)
            },
        )

        CategoryTitle(
            modifier = modifier.constrainAs(categoryTitle) {
                top.linkTo(profileImage.bottom, margin = 8.dp)
                start.linkTo(profileImage.start)
                end.linkTo(parent.end, margin = 32.dp)
                width = Dimension.fillToConstraints
            },
            title = categoryInvitation.categoryTitle,
        )

        Text(
            text = "님이 카테고리에 초대했어요.",
            modifier = modifier.constrainAs(guideText) {
                start.linkTo(inviterNickname.end)
                centerVerticallyTo(inviterNickname)
                width = Dimension.wrapContent
            } ,
            style = Body4,
            color = Gray5,
        )

        RejectButton(
            modifier = modifier.constrainAs(rejectButton) {
                bottom.linkTo(parent.bottom, margin = 16.dp)
                end.linkTo(acceptButton.start, margin = 4.dp)
            },
            onClick = { onRejectClick(categoryInvitation.invitationId) },
        )

        AcceptButton(
            modifier = modifier.constrainAs(acceptButton) {
                top.linkTo(categoryTitle.bottom, margin = 8.dp)
                bottom.linkTo(parent.bottom, margin = 16.dp)
                end.linkTo(parent.end, margin = 22.dp)
            },
            onClick = { onAcceptClick(categoryInvitation.invitationId) },
        )
    }
}

@Composable
private fun AcceptButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    DefaultTextButton(
        modifier = modifier,
        text = "수락",
        onClick = onClick,
        backgroundColor = StaccatoBlue,
        textColor = White,
    )
}

@Composable
private fun RejectButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    DefaultTextButton(
        modifier = modifier,
        text = "거절",
        onClick = onClick,
        backgroundColor = White,
        textColor = StaccatoBlack,
        border = BorderStroke(
            width = 0.5.dp,
            color = Gray2,
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AcceptButtonPreview() {
    Box(modifier = Modifier.padding(10.dp)) {
        DefaultTextButton(
            text = "수락",
            onClick = { },
            backgroundColor = StaccatoBlue,
            textColor = White
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RejectButtonPreview() {
    Box(modifier = Modifier.padding(10.dp)) {
        DefaultTextButton(
            text = "거절",
            onClick = { },
            backgroundColor = White,
            textColor = StaccatoBlack,
            border = BorderStroke(
                width = 0.5.dp,
                color = Gray2,
            )
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0L,
)
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

class CategoryPreviewProvider : PreviewParameterProvider<ReceivedInvitationUiModel> {
    override val values: Sequence<ReceivedInvitationUiModel>
        get() = dummyReceivedInvitationUiModels.asSequence()
}
