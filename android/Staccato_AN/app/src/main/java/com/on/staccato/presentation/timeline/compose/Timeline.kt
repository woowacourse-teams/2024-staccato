package com.on.staccato.presentation.timeline.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.theme.Gray1
import java.time.LocalDate

@Composable
fun Timeline(timeline: List<TimelineUiModel>) {
    LazyColumn(
        contentPadding = PaddingValues(2.dp),
    ) {
        items(timeline) { timelineCategory ->
            TimelineItem(timeline = timelineCategory)
            HorizontalDivider(thickness = 1.dp, color = Gray1)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TimelinePreview() {
    Timeline(timeline = timeline)
}

val timeline =
    listOf(
        TimelineUiModel(
            categoryId = 1,
            categoryThumbnailUrl = null,
            categoryTitle = "카테고리 제목",
            startAt = null,
            endAt = null,
            color = com.on.staccato.presentation.common.color.CategoryColor.GRAY.colorRes,
        ),
        TimelineUiModel(
            categoryId = 2,
            categoryThumbnailUrl = null,
            categoryTitle = "카테고리 제목",
            startAt = LocalDate.of(2025, 1, 1),
            endAt = LocalDate.of(2025, 12, 31),
            color = CategoryColor.GRAY.colorRes,
        ),
        // TODO: 서버 API 변경 되면 개인 카테고리 Preview 작성(함께하는 사람들 X)
    )
