package com.on.staccato.presentation.timeline.compose

import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.TextComponent
import com.on.staccato.theme.Body6
import com.on.staccato.theme.White

@Composable
fun HiddenMembersCountItem(
    count: Int,
    @ColorRes color: Int,
) {
    Box(
        modifier =
            Modifier
                .size(17.dp)
                .shadow(2.dp, shape = CircleShape, clip = false)
                .background(color = colorResource(id = color), shape = CircleShape)
                .border(width = 1.dp, color = White, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        TextComponent(description = "+$count", color = White, style = Body6)
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
        color = R.color.gray3,
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
