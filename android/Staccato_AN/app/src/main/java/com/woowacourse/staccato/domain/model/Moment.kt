package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class Moment(
    val momentId: Long,
    val placeName: String,
    val momentImageUrls: List<String>,
    val address: String,
    val visitedAt: LocalDate,
    val comments: List<VisitLog>,
)
