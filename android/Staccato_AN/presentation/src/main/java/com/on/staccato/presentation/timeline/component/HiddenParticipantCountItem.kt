package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.theme.Body6
import com.on.staccato.theme.White

@Composable
fun HiddenParticipantCountItem(
    count: Long,
    color: CategoryColor,
) {
    Box(
        modifier =
            Modifier
                .size(21.dp)
                .shadow(2.dp, shape = CircleShape, clip = true)
                .background(color = White, shape = CircleShape)
                .padding(1.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = color.color, shape = CircleShape),
        ) {
            Text(
                text = "+$count",
                color = color.textColor,
                style = Body6,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HiddenParticipantCountPreview(
    @PreviewParameter(HiddenParticipantCountPreviewParameterProvider::class)
    count: Long,
) {
    HiddenParticipantCountItem(
        count = count,
        color = CategoryColor.RED,
    )
}

private class HiddenParticipantCountPreviewParameterProvider : PreviewParameterProvider<Long> {
    override val values: Sequence<Long> =
        sequenceOf(
            3L,
            10L,
            16L,
        )
}
