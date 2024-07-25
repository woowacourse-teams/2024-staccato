package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class Visit(
    val visitId: Long,
    val placeName: String,
    val visitImages: List<String>,
    val address: String,
    val visitedAt: LocalDate,
    val visitedCount: Long,
    val visitLogs: List<VisitLog>,
)
