package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class Visit(
    val visitId: Long,
    val placeName: String,
    val visitImageUrls: List<String>,
    val address: String,
    val visitedAt: LocalDate,
    val visitLogs: List<VisitLog>,
)
