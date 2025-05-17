package com.on.staccato.presentation.timeline.model

import androidx.annotation.ColorRes
import com.on.staccato.presentation.common.MemberUiModel
import com.on.staccato.presentation.common.color.CategoryColor
import com.on.staccato.presentation.common.dummyMembersUiModel
import java.time.LocalDate

data class TimelineUiModel(
    val categoryId: Long,
    val categoryTitle: String,
    val categoryThumbnailUrl: String? = null,
    @ColorRes val color: Int,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val isShared: Boolean,
    val participants: List<MemberUiModel>,
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
            color = com.on.staccato.presentation.common.color.CategoryColor.GRAY.colorRes,
            participants = dummyMembersUiModel,
            staccatoCount = 10,
        ),
        TimelineUiModel(
            categoryId = 2,
            categoryThumbnailUrl = null,
            categoryTitle = "카테고리 제목 2",
            startAt = LocalDate.of(2025, 1, 1),
            endAt = LocalDate.of(2025, 12, 31),
            isShared = true,
            color = CategoryColor.GRAY.colorRes,
            participants = dummyMembersUiModel,
            staccatoCount = 100,
        ),
        TimelineUiModel(
            categoryId = 3,
            categoryThumbnailUrl = null,
            categoryTitle = "아주아주아주아주아주아주아주아주아주아주아주긴카테고리제목~",
            startAt = LocalDate.of(2025, 1, 1),
            endAt = LocalDate.of(2025, 12, 31),
            isShared = true,
            color = CategoryColor.GRAY.colorRes,
            participants = dummyMembersUiModel,
            staccatoCount = 10,
        ),
        TimelineUiModel(
            categoryId = 4,
            categoryThumbnailUrl = null,
            categoryTitle = "아주아주아주아주아주아주아주아주아주아주아주긴카테고리제목~",
            startAt = LocalDate.of(2025, 1, 1),
            endAt = LocalDate.of(2025, 12, 31),
            isShared = false,
            color = CategoryColor.GRAY.colorRes,
            participants = dummyMembersUiModel,
            staccatoCount = 10,
        ),
    )
