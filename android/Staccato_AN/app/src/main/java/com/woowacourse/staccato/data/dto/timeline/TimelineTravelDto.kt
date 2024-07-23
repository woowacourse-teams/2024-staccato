package com.woowacourse.staccato.data.dto.timeline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineTravelDto(
    @SerialName("travelId") val travelId: Long,
    @SerialName("travelTitle") val travelTitle: String,
    @SerialName("travelThumbnail") val travelThumbnail: String,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
    @SerialName("mates") val mates: List<TimelineMemberDto>,
)
