package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import com.on.staccato.presentation.common.color.CategoryColor.Companion.getColorBy
import com.on.staccato.presentation.common.color.CategoryColor.Companion.getTextColorBy
import com.on.staccato.theme.Body6
import com.on.staccato.theme.White

@Composable
fun HiddenMembersCountItem(
    count: Int,
    colorLabel: String,
) {
    Box(
        modifier =
            Modifier
                .size(19.dp)
                .shadow(2.dp, shape = CircleShape, clip = false)
                .background(color = getColorBy(colorLabel), shape = CircleShape)
                .border(width = 1.dp, color = White, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+$count",
            color = getTextColorBy(colorLabel),
            style = Body6,
        )
    }
}

@Preview
@Composable
private fun HiddenMembersCountPreview(
    @PreviewParameter(HiddenMembersCountPreviewParameterProvider::class)
    count: Int,
) {
    HiddenMembersCountItem(
        count = count,
        colorLabel = CategoryColor.GRAY.label,
    )
}

private class HiddenMembersCountPreviewParameterProvider : PreviewParameterProvider<Int> {
    override val values: Sequence<Int> =
        sequenceOf(
            3,
            10,
            88,
        )
}
