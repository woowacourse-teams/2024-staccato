package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.timeline.model.ParticipantsUiModel
import com.on.staccato.presentation.timeline.model.ParticipantsUiModel.Companion.MIN_HIDDEN_COUNT
import com.on.staccato.presentation.timeline.model.dummyParticipantsUiModels

@Composable
fun Participants(
    modifier: Modifier = Modifier,
    participants: ParticipantsUiModel,
    colorLabel: String,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(2.dp),
        horizontalArrangement = Arrangement.spacedBy((-8).dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(participants.profileImageUrls) { profileImageUrl ->
            ParticipantItem(profileImageUrl)
        }

        if (participants.hiddenCount != MIN_HIDDEN_COUNT) {
            item {
                HiddenParticipantCountItem(participants.hiddenCount, colorLabel = colorLabel)
            }
        }
    }
}

@Preview
@Composable
private fun ParticipantsPreview(
    @PreviewParameter(ParticipantsPreviewParameterProvider::class)
    participants: ParticipantsUiModel,
) {
    Participants(
        participants = participants,
        colorLabel = CategoryColor.GRAY.label,
    )
}

private class ParticipantsPreviewParameterProvider(
    override val values: Sequence<ParticipantsUiModel> =
        sequenceOf(*dummyParticipantsUiModels.toTypedArray()),
) : PreviewParameterProvider<ParticipantsUiModel>
