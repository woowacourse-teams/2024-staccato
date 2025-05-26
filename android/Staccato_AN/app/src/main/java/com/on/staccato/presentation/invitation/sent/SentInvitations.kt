package com.on.staccato.presentation.invitation.sent

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.invitation.model.SentInvitationUiModel
import com.on.staccato.presentation.invitation.model.dummySentInvitationUiModels
import com.on.staccato.theme.Body4

val LocalGuideTextWidth = compositionLocalOf<Dp> { 80.dp }

@Composable
fun SentInvitations(
    sentInvitations: List<SentInvitationUiModel>,
    onCancelClick: (invitationId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val suffixTextWidth = remember {
        textMeasurer.measure(
            text = AnnotatedString("에 초대했어요."),
            style = Body4,
        ).size.width
    }
    val suffixTextWidthDp = with(density) { suffixTextWidth.toDp() }

    CompositionLocalProvider(LocalGuideTextWidth provides suffixTextWidthDp) {
        LazyColumn(modifier = modifier) {
            itemsIndexed(sentInvitations) { index, invitation ->
                SentInvitationItem(
                    categoryInvitation = invitation,
                    onCancelClick = onCancelClick,
                )
                if (index != sentInvitations.lastIndex) {
                    DefaultDivider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SentInvitationPreview() {
    SentInvitations(
        sentInvitations = dummySentInvitationUiModels,
        onCancelClick = {},
    )
}
