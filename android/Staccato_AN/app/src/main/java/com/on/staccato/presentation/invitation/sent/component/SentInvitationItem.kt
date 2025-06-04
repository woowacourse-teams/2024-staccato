package com.on.staccato.presentation.invitation.sent.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.on.staccato.R
import com.on.staccato.presentation.invitation.component.CategoryTitle
import com.on.staccato.presentation.invitation.component.NicknameText
import com.on.staccato.presentation.invitation.component.ProfileImage
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel
import com.on.staccato.presentation.invitation.model.dummySentInvitationLongTitle
import com.on.staccato.presentation.invitation.model.dummySentInvitationUiModels
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
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = White,
                ),
    ) {
        val (profileImageRef, nicknameRef, titleRef, suffixRef, cancelButtonRef) = createRefs()
        val titleChain = createHorizontalChain(titleRef, suffixRef, chainStyle = ChainStyle.Packed(bias = 0f))
        constrain(titleChain) {
            start.linkTo(profileImageRef.end, margin = 10.dp)
            end.linkTo(cancelButtonRef.start, margin = 22.dp)
        }

        ProfileImage(
            modifier =
                modifier
                    .size(40.dp)
                    .constrainAs(profileImageRef) {
                        start.linkTo(parent.start, margin = 20.dp)
                        top.linkTo(parent.top, margin = 20.dp)
                        bottom.linkTo(parent.bottom, margin = 20.dp)
                    },
            url = categoryInvitation.inviteeProfileImageUrl,
        )

        NicknameText(
            modifier =
                modifier.constrainAs(nicknameRef) {
                    start.linkTo(profileImageRef.end, margin = 10.dp)
                    top.linkTo(profileImageRef.top)
                    bottom.linkTo(titleRef.top)
                },
            nickname = categoryInvitation.inviteeNickname,
            style = Title3,
            color = StaccatoBlack,
        )

        CategoryTitle(
            modifier =
                modifier.constrainAs(titleRef) {
                    top.linkTo(nicknameRef.bottom)
                    bottom.linkTo(profileImageRef.bottom)
                    end.linkTo(suffixRef.start)
                    width = Dimension.preferredWrapContent
                },
            title = categoryInvitation.categoryTitle,
            style = Body4,
            color = Gray3,
        )

        Text(
            modifier =
                modifier.constrainAs(suffixRef) {
                    centerVerticallyTo(titleRef)
                    start.linkTo(titleRef.end)
                    width = Dimension.wrapContent
                },
            text = stringResource(id = R.string.invitation_management_guide_text_category),
            style = Body4,
            color = Gray3,
            maxLines = 1,
        )

        CancelButton(
            modifier =
                modifier.constrainAs(cancelButtonRef) {
                    end.linkTo(parent.end, margin = 20.dp)
                    centerVerticallyTo(parent)
                },
            onClick = { onCancelClick(categoryInvitation.invitationId) },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 1L)
@Composable
private fun SentInvitationItemPreview(
    @PreviewParameter(
        provider = SentInvitationPreviewProvider::class,
    ) categoryInvitation: SentInvitationUiModel,
) {
    Box(modifier = Modifier.padding(10.dp)) {
        SentInvitationItem(
            categoryInvitation = categoryInvitation,
            onCancelClick = {},
        )
    }
}

private class SentInvitationPreviewProvider(
    override val values: Sequence<SentInvitationUiModel> = dummySentInvitationUiModels.asSequence(),
) : PreviewParameterProvider<SentInvitationUiModel>

@Preview(name = "제목이 긴 경우", showBackground = true, backgroundColor = 1L)
@Composable
private fun LongTitleItemPreview() {
    Box(modifier = Modifier.padding(10.dp)) {
        SentInvitationItem(
            categoryInvitation = dummySentInvitationLongTitle,
            onCancelClick = {},
        )
    }
}
