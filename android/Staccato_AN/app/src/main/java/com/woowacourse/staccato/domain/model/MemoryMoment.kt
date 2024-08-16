package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class MemoryMoment(
    val momentId: Long,
    val placeName: String,
    val momentImageUrl: String? = null,
    val visitedAt: LocalDate,
)
