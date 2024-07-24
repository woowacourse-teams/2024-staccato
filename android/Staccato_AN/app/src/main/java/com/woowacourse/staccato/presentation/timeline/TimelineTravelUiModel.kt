package com.woowacourse.staccato.presentation.timeline

data class TimelineTravelUiModel(
    val travelId: Long,
    val travelThumbnail: String? = null,
    val travelPeriod: String,
    val travelTitle: String,
)
