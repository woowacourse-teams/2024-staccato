package com.on.staccato.domain.model

import java.time.LocalDateTime

data class StaccatoLocation(
    val staccatoId: Long,
    val latitude: Double,
    val longitude: Double,
    val color: String,
    val title: String,
    val visitedAt: LocalDateTime,
)
