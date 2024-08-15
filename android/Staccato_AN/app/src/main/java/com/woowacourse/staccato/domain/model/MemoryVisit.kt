package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class MemoryVisit(
    val visitId: Long,
    val placeName: String,
    val visitImageUrl: String? = null,
    val visitedAt: LocalDate,
)
