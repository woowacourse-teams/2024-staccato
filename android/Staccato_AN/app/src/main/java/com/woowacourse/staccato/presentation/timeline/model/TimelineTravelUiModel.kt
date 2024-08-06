package com.woowacourse.staccato.presentation.timeline.model

import java.time.LocalDate

data class TimelineTravelUiModel(
    val id: Long,
    val title: String,
    val thumbnail: String? = null,
    val startAt: LocalDate,
    val endAt: LocalDate,
)
