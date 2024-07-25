package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class Travel(
    val travelId: Long,
    val travelThumbnail: String,
    val travelTitle: String,
    val startAt: LocalDate,
    val endAt: LocalDate,
    val description: String,
    val mates: List<Member>,
    val visits: List<Visit>,
)
