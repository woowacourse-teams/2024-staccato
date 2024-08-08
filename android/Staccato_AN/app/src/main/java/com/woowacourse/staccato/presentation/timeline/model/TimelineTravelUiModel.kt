package com.woowacourse.staccato.presentation.timeline.model

import java.time.LocalDate

data class TimelineTravelUiModel(
    val travelId: Long,
    val travelTitle: String,
    val travelThumbnailUrl: String? = null,
    val startAt: LocalDate,
    val endAt: LocalDate,
)
