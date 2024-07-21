package com.woowacourse.staccato.data.dto.travel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTravelDto(
    @SerialName("travel_thumbnail") val travelThumbnail: String? = null,
    @SerialName("travel_title") val travelTitle: String,
    @SerialName("description") val description: String? = null,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
)
