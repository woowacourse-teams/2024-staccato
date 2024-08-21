package com.woowacourse.staccato.domain.model

import java.time.LocalDateTime

data class Moment(
    val momentId: Long,
    val placeName: String,
    val momentImageUrls: List<String>,
    val address: String,
    val visitedAt: LocalDateTime,
    val comments: List<VisitLog>,
)
