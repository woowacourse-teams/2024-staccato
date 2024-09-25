package com.on.staccato.presentation.moment.detail

import java.time.LocalDateTime

data class MomentDetailUiModel(
    val id: Long,
    val memoryId: Long,
    val memoryTitle: String,
    val staccatoTitle: String,
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
    val momentImageUrls: List<String>,
    val address: String,
    val visitedAt: LocalDateTime,
)
