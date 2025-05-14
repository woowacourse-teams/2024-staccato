package com.on.staccato.presentation.timeline.model

import androidx.annotation.ColorRes
import com.on.staccato.presentation.common.color.CategoryColor
import java.time.LocalDate

data class TimelineUiModel(
    val categoryId: Long,
    val categoryTitle: String,
    val categoryThumbnailUrl: String? = null,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    @ColorRes val color: Int,
)

val dummyTimelineUiModel =
    listOf(
        TimelineUiModel(
            categoryId = 1,
            categoryThumbnailUrl = null,
            categoryTitle = "카테고리 제목 1",
            startAt = null,
            endAt = null,
            color = com.on.staccato.presentation.common.color.CategoryColor.GRAY.colorRes,
        ),
        TimelineUiModel(
            categoryId = 2,
            categoryThumbnailUrl = null,
            categoryTitle = "카테고리 제목 2",
            startAt = LocalDate.of(2025, 1, 1),
            endAt = LocalDate.of(2025, 12, 31),
            color = CategoryColor.GRAY.colorRes,
        ),
        // TODO: 서버 API 변경 되면 개인 카테고리 Preview 데이터 작성(함께하는 사람들 X)
    )
