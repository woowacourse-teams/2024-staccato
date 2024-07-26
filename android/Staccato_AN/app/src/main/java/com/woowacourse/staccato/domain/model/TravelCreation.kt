package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class TravelCreation(
    val travelThumbnail: String? = null,
    val travelTitle: String,
    val startAt: LocalDate,
    val endAt: LocalDate,
    val description: String? = null,
)
