package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class TravelVisit(
    val visitId: Long,
    val placeName: String,
    val visitImage: String,
    val visitedAt: LocalDate,
)