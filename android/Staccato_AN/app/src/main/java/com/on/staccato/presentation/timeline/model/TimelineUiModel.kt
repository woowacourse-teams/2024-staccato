package com.on.staccato.presentation.timeline.model

import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.dummyMembers
import com.on.staccato.presentation.common.color.CategoryColor
import java.time.LocalDate

data class TimelineUiModel(
    val categoryId: Long,
    val categoryTitle: String,
    val categoryThumbnailUrl: String? = null,
    val color: CategoryColor,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val isShared: Boolean,
    val participants: List<Member>,
    val staccatoCount: Int,
)

val dummyTimelineUiModel =
    listOf(
        TimelineUiModel(
            categoryId = 1,
            categoryThumbnailUrl = null,
            categoryTitle = "카테고리 제목 1",
            startAt = null,
            endAt = null,
            isShared = true,
            color = CategoryColor.GRAY,
            participants = dummyMembers,
            staccatoCount = 10,
        ),
        TimelineUiModel(
            categoryId = 2,
            categoryThumbnailUrl = null,
            categoryTitle = "카테고리 제목 2",
            startAt = LocalDate.of(2025, 1, 1),
            endAt = LocalDate.of(2025, 12, 31),
            isShared = true,
            color = CategoryColor.GRAY,
            participants = dummyMembers,
            staccatoCount = 100,
        ),
        TimelineUiModel(
            categoryId = 3,
            categoryThumbnailUrl = null,
            categoryTitle = "아주아주아주아주아주아주아주아주아주아주아주긴카테고리제목~",
            startAt = LocalDate.of(2025, 1, 1),
            endAt = LocalDate.of(2025, 12, 31),
            isShared = true,
            color = CategoryColor.GRAY,
            participants = dummyMembers,
            staccatoCount = 10,
        ),
        TimelineUiModel(
            categoryId = 4,
            categoryThumbnailUrl = null,
            categoryTitle = "아주아주아주아주아주아주아주아주아주아주아주긴카테고리제목~",
            startAt = LocalDate.of(2025, 1, 1),
            endAt = LocalDate.of(2025, 12, 31),
            isShared = false,
            color = CategoryColor.GRAY,
            participants = dummyMembers,
            staccatoCount = 10,
        ),
    )
