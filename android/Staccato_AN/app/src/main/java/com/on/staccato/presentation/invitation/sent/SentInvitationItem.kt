package com.on.staccato.presentation.invitation.sent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.on.staccato.presentation.invitation.component.CategoryTitle
import com.on.staccato.presentation.invitation.component.NicknameText
import com.on.staccato.presentation.invitation.component.ProfileImage
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel
import com.on.staccato.presentation.invitation.model.dummySentInvitationUiModels
import com.on.staccato.presentation.invitation.sent.component.CancelButton
import com.on.staccato.presentation.invitation.sent.component.CategoryTitleLayout
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.Title3
import com.on.staccato.theme.White

@Composable
fun SentInvitationItem(
    modifier: Modifier = Modifier,
    categoryInvitation: SentInvitationUiModel,
    onCancelClick: (invitationId: Long) -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
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
                    start.linkTo(parent.start, margin = 20.dp)
                    top.linkTo(parent.top, margin = 20.dp)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                },
            url = categoryInvitation.inviteeProfileImageUrl,
        )

        Column(
            modifier = modifier.constrainAs(inviteeNicknameWithCategoryTitle) {
                start.linkTo(inviteeProfileImage.end, margin = 10.dp)
                end.linkTo(cancelButton.start, margin = 22.dp)
                centerVerticallyTo(inviteeProfileImage)
                width = Dimension.fillToConstraints
            }
        ) {
            NicknameText(
                nickname = categoryInvitation.inviteeNickname,
                style = Title3,
                color = StaccatoBlack,
            )

            CategoryTitleLayout(
                categoryTitle = categoryInvitation.categoryTitle,
            )
        }

        CancelButton(
            modifier = modifier
                .constrainAs(cancelButton) {
                    end.linkTo(parent.end, margin = 20.dp)
                    centerVerticallyTo(parent)
                },
            onClick = { onCancelClick(categoryInvitation.invitationId) },
        )
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
        provider = SentInvitationPreviewProvider::class,
    ) categoryInvitation: SentInvitationUiModel,
) {
    Box(modifier = Modifier.padding(10.dp)) {
        SentInvitationItem(categoryInvitation = categoryInvitation) {}
    }
}

class SentInvitationPreviewProvider : PreviewParameterProvider<SentInvitationUiModel> {
    override val values: Sequence<SentInvitationUiModel>
        get() = dummySentInvitationUiModels.asSequence()
}
