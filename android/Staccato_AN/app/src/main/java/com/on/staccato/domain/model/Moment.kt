package com.on.staccato.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Moment(
    val momentId: Long,
    val staccatoTitle: String,
    val placeName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val momentImageUrls: List<String>,
    val memoryId: Long,
    val memoryTitle: String,
    val visitedAt: LocalDateTime,
    val startAt: LocalDate?,
    val endAt: LocalDate?,
    val feeling: Feeling,
)
