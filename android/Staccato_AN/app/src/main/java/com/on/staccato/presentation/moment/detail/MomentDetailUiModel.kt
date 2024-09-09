package com.on.staccato.presentation.moment.detail

import java.time.LocalDateTime

data class MomentDetailUiModel(
    val id: Long,
    val memoryId: Long,
    val memoryTitle: String,
    val placeName: String,
    val momentImageUrls: List<String>,
    val address: String,
    val visitedAt: LocalDateTime,
)
