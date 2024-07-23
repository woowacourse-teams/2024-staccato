package com.woowacourse.staccato.data.dto.travel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTravelDto(
    @SerialName("travelThumbnail") val travelThumbnail: String? = null,
    @SerialName("travelTitle") val travelTitle: String,
    @SerialName("description") val description: String? = null,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
)
