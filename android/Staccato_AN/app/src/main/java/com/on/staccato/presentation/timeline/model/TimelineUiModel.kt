package com.on.staccato.presentation.timeline.model

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
    val participants: ParticipantsUiModel,
    val staccatoCount: Long,
)

val dummyTimelineUiModel =
    TimelineUiModel(
        categoryId = 1,
        categoryThumbnailUrl = null,
        categoryTitle = "카테고리 제목",
        startAt = LocalDate.of(2025, 1, 1),
        endAt = LocalDate.of(2025, 12, 31),
        isShared = true,
        color = CategoryColor.GRAY,
        participants = dummyParticipantsUiModels[3],
        staccatoCount = 100,
    )

val dummyTimelineUiModels: List<TimelineUiModel> =
    listOf(
        dummyTimelineUiModel.copy(
            categoryId = 1L,
            categoryTitle = "기간이 없는 공동 카테고리",
            startAt = null,
            endAt = null,
            participants = dummyParticipantsUiModels[0],
            color = CategoryColor.LIGHT_MINT,
        ),
        dummyTimelineUiModel.copy(
            categoryId = 2L,
            categoryTitle = "기간이 있는 공동 카테고리",
            participants = dummyParticipantsUiModels[1],
            color = CategoryColor.LIGHT_BLUE,
        ),
        dummyTimelineUiModel.copy(
            categoryId = 3L,
            categoryTitle = "아주아주 긴 제목을 가진 기간이 있는 공동 카테고리~~",
            participants = dummyParticipantsUiModels[2],
            color = CategoryColor.LIGHT_INDIGO,
        ),
        dummyTimelineUiModel.copy(
            categoryId = 4L,
            categoryTitle = "기간이 없는 개인 카테고리",
            startAt = null,
            endAt = null,
            isShared = false,
            color = CategoryColor.LIGHT_PURPLE,
        ),
        dummyTimelineUiModel.copy(
            categoryId = 5L,
            categoryTitle = "기간이 있는 개인 카테고리",
            isShared = false,
        ),
        dummyTimelineUiModel.copy(
            categoryId = 6L,
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 개인 카테고리~~",
            startAt = null,
            endAt = null,
            isShared = false,
        ),
    )
