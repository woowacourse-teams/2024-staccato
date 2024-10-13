package com.on.staccato.presentation.staccato.detail

import java.time.LocalDateTime

data class StaccatoDetailUiModel(
    val id: Long,
    val memoryId: Long,
    val memoryTitle: String,
    val staccatoTitle: String,
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
    val staccatoImageUrls: List<String>,
    val address: String,
    val visitedAt: LocalDateTime,
)
