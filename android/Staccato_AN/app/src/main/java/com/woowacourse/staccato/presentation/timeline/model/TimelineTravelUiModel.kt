package com.woowacourse.staccato.presentation.timeline.model

import java.time.LocalDate

data class TimelineMemoryUiModel(
    val MemoryId: Long,
    val MemoryTitle: String,
    val MemoryThumbnailUrl: String? = null,
    val startAt: LocalDate,
    val endAt: LocalDate,
)
