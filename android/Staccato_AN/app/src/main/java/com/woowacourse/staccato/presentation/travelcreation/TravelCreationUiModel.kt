package com.woowacourse.staccato.presentation.travelcreation

import java.time.LocalDate

data class TravelCreationUiModel(
    val travelThumbnail: String? = null,
    val travelTitle: String,
    val startAt: LocalDate,
    val endAt: LocalDate,
    val description: String? = null,
)
