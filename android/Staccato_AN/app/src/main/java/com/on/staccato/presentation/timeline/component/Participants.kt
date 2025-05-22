package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.dummyMembers
import com.on.staccato.presentation.common.color.CategoryColor

@Composable
fun Participants(
    modifier: Modifier = Modifier,
    participants: List<Member>,
    hiddenParticipantsCount: Int,
    colorLabel: String,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(2.dp),
        horizontalArrangement = Arrangement.spacedBy((-8).dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(participants) { participant ->
            ParticipantItem(profileImageUrl = participant.memberImage)
        }

        item {
            HiddenParticipantsCountItem(hiddenParticipantsCount, colorLabel = colorLabel)
        }
    }
}

@Preview
@Composable
private fun ParticipantsPreview() {
    Participants(
        participants = dummyMembers,
        hiddenParticipantsCount = 3,
        colorLabel = CategoryColor.GRAY.label,
    )
}
