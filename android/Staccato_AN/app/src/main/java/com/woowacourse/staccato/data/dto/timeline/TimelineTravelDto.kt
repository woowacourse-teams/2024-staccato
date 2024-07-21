package com.woowacourse.staccato.data.dto.timeline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineTravelDto(
    @SerialName("travel_id") val travelId: Long,
    @SerialName("travel_title") val travelTitle: String,
    @SerialName("travel_thumbnail") val travelThumbnail: String,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
    @SerialName("mates") val mates: List<TimelineMemberDto>,
)
