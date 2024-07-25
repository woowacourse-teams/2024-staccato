package com.woowacourse.staccato.domain.model

data class Visit(
    val visitId: Long,
    val placeName: String,
    val visitedImages: List<String>,
    val address: String,
    val visitedAt: String,
    val visitedCount: Long,
    val visitLogs: List<VisitLog>,
)
