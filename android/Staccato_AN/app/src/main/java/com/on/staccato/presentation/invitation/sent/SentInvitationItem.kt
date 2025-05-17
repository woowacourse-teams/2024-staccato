package com.on.staccato.presentation.invitation.sent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.on.staccato.presentation.component.DefaultTextButton
import com.on.staccato.presentation.invitation.component.CategoryTitle
import com.on.staccato.presentation.invitation.component.NicknameText
import com.on.staccato.presentation.invitation.component.ProfileImage
import com.on.staccato.presentation.invitation.model.InvitationUiModel
import com.on.staccato.presentation.invitation.received.CategoryPreviewProvider
import com.on.staccato.theme.Accents4
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.Title3
import com.on.staccato.theme.White

@Composable
fun SentInvitationItem(
    modifier: Modifier = Modifier,
    categoryInvitation: InvitationUiModel,
    onCancelClick: () -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
            .height(80.dp)
            .fillMaxWidth()
            .background(
                color = White,
            ),
    ) {
        val (inviteeProfileImage, inviteeNicknameWithCategoryTitle, cancelButton) = createRefs()

        ProfileImage(
            modifier = modifier
                .size(40.dp)
                .constrainAs(inviteeProfileImage) {
                    centerVerticallyTo(parent)
                    start.linkTo(parent.start, margin = 20.dp)
                },
            url = categoryInvitation.inviterProfileImageUrl,
        )

        Column(
            modifier = modifier.constrainAs(inviteeNicknameWithCategoryTitle) {
                centerVerticallyTo(inviteeProfileImage)
                start.linkTo(inviteeProfileImage.end, margin = 10.dp)
                end.linkTo(cancelButton.start, margin = 16.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            NicknameText(
                nickname = categoryInvitation.inviterNickname,
                style = Title3,
                color = StaccatoBlack,
            )

            CategoryTitle(
                title = categoryInvitation.categoryTitle,
                style = Body4,
                color = Gray3,
            )
        }

        CancelButton(
            modifier = modifier.constrainAs(cancelButton) {
                centerVerticallyTo(parent)
                end.linkTo(parent.end, margin = 20.dp)
            },
            onClick = onCancelClick,
        )
    }
}

@Composable
private fun CancelButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    DefaultTextButton(
        modifier = modifier,
        text = "취소",
        onClick = onClick,
        backgroundColor = Accents4,
        textColor = White,
    )
}

@Preview(showBackground = true)
@Composable
private fun CancelButtonPreview() {
    Box(modifier = Modifier.padding(10.dp)) {
        CancelButton {}
    }
}

@Preview(showBackground = true)
@Composable
private fun InviteeNicknameWithCategoryTitlePreview() {
    Column(
        modifier = Modifier.width(150.dp)
    ) {
        NicknameText(
            nickname = "10글자짜리닉네임임",
            style = Title3,
            color = StaccatoBlack,
            )

        CategoryTitle(
            title = "카테고리제목근데이제엄청나게긴",
            style = Body4,
            color = Gray3,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 1L,
)
@Composable
private fun SentInvitationItemPreview(
    @PreviewParameter(
        provider = CategoryPreviewProvider::class,
    ) categoryInvitation: InvitationUiModel,
) {
    Box(modifier = Modifier.padding(10.dp)) {
        SentInvitationItem(categoryInvitation = categoryInvitation) {}
    }
}
