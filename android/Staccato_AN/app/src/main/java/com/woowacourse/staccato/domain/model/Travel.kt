package com.woowacourse.staccato.domain.model

import java.time.LocalDate

data class Travel(
    val travelId: Long,
    val travelThumbnailUrl: String? = null,
    val travelTitle: String,
    val startAt: LocalDate,
    val endAt: LocalDate,
    val description: String? = null,
    val mates: List<Member>,
    val visits: List<TravelVisit>,
)
