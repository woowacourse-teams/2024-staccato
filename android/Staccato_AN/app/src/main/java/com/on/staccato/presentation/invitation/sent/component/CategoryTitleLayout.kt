package com.on.staccato.presentation.invitation.sent.component

import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.invitation.component.CategoryTitle
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3

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
