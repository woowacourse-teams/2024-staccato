package com.woowacourse.staccato.presentation.moment.detail

import java.time.LocalDateTime

data class MomentDetailUiModel(
    val id: Long,
    val placeName: String,
    val momentImageUrls: List<String>,
    val address: String,
    val visitedAt: LocalDateTime,
)
