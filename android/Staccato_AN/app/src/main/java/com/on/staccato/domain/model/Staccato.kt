package com.on.staccato.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Staccato(
    val staccatoId: Long,
    val staccatoTitle: String,
    val placeName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val staccatoImageUrls: List<String>,
    val categoryId: Long,
    val categoryTitle: String,
    val visitedAt: LocalDateTime,
    val startAt: LocalDate?,
    val endAt: LocalDate?,
    val feeling: Feeling,
)
