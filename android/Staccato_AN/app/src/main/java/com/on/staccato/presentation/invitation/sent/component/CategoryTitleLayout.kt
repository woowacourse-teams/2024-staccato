package com.on.staccato.presentation.invitation.sent.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.invitation.component.CategoryTitle
import com.on.staccato.presentation.invitation.sent.LocalGuideTextWidth
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3

@Composable
fun CategoryTitleLayout(
    categoryTitle: String,
    modifier: Modifier = Modifier,
    endMargin: Dp = 0.dp,
) {
    val density = LocalDensity.current
    val guideTextWidth = LocalGuideTextWidth.current

    BoxWithConstraints(modifier = modifier) {
        val titleWidth = remember(maxWidth, guideTextWidth) {
            with(density) {
                val usableWidth = maxWidth - (guideTextWidth + endMargin)
                usableWidth.coerceAtLeast(0.dp)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryTitle(
                title = categoryTitle,
                modifier = Modifier.widthIn(max = titleWidth),
                style = Body4,
                color = Gray3,
            )

            Text(
                text = "에 초대했어요.",
                maxLines = 1,
                style = Body4,
                color = Gray3,
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0L,
)
private fun CategoryTitleLayoutPreview(
    @PreviewParameter(CategoryTitlePreviewProvider::class) categoryTitle: String,
) {
    Box(
        modifier = Modifier
            .width(250.dp)
            .background(
                color = Color.White
            )
    ) {
        CategoryTitleLayout(
            categoryTitle = categoryTitle,
        )
    }
}

private class CategoryTitlePreviewProvider(
    override val values: Sequence<String> =
        sequenceOf(
            "짧은 제목",
            "적당한 길이의 제목",
            "무지막지하게 기이이이이이이이이인 제목",
            "퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁퉁",
            "무지무지무지무지무지무지무지무지무지무지 기이이이이인 제목",
        )
) : PreviewParameterProvider<String>
