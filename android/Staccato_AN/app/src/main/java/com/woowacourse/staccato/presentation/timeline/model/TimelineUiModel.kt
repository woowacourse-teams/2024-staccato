package com.woowacourse.staccato.presentation.timeline.model

import java.time.LocalDate

data class TimelineUiModel(
    val memoryId: Long,
    val memoryTitle: String,
    val memoryThumbnailUrl: String? = null,
    val startAt: LocalDate,
    val endAt: LocalDate,
)
