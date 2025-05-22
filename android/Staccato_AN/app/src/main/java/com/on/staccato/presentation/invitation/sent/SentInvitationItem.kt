package com.on.staccato.presentation.invitation.sent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.on.staccato.presentation.component.DefaultTextButton
import com.on.staccato.presentation.invitation.component.CategoryTitle
import com.on.staccato.presentation.invitation.component.NicknameText
import com.on.staccato.presentation.invitation.component.ProfileImage
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel
import com.on.staccato.presentation.invitation.model.dummySentInvitationUiModels
import com.on.staccato.theme.Accents4
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.Title3
import com.on.staccato.theme.White

@Composable
fun SentInvitationItem(
    modifier: Modifier = Modifier,
    categoryInvitation: SentInvitationUiModel,
    onCancelClick: () -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = White,
            ),
    ) {
        val (inviteeProfileImage, inviteeNicknameWithCategoryTitle, cancelButton) = createRefs()
        var cancelButtonStartX by remember { mutableFloatStateOf(0f) }

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
                cancelButtonStartX = cancelButtonStartX,
            )
        }

        CancelButton(
            modifier = modifier.constrainAs(cancelButton) {
                end.linkTo(parent.end, margin = 20.dp)
                centerVerticallyTo(parent)
            }.onGloballyPositioned { coordinates ->
                cancelButtonStartX = coordinates.positionInParent().x
            },
            onClick = onCancelClick,
        )
    }
}

@Composable
fun CategoryTitleLayout(
    categoryTitle: String,
    modifier: Modifier = Modifier,
    cancelButtonStartX: Float,
    endMargin: Dp = 22.dp,
) {
    val density = LocalDensity.current

    var layoutStartX by remember { mutableFloatStateOf(0f) }
    var text2WidthPx by remember { mutableIntStateOf(0) }

    val titleMaxWidthDp = remember(layoutStartX, text2WidthPx) {
        with(density) {
            val usableWidthPx = cancelButtonStartX - layoutStartX - text2WidthPx - endMargin.toPx()
            usableWidthPx.coerceAtLeast(0f).toDp()
        }
    }

    Row(
        modifier = modifier.onGloballyPositioned { coordinates ->
                layoutStartX = coordinates.positionInRoot().x
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryTitle(
            title = categoryTitle,
            modifier = Modifier.widthIn(
                max = titleMaxWidthDp
            ),
            style = Body4,
            color = Gray3,
        )

        Text(
            text = "에 초대했어요.",
            modifier = Modifier
                .onGloballyPositioned {
                    text2WidthPx = it.size.width
                },
            maxLines = 1,
            style = Body4,
            color = Gray3,
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
