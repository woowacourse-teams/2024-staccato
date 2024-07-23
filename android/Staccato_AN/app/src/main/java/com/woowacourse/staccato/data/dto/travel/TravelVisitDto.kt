package com.woowacourse.staccato.data.dto.travel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TravelVisitDto(
    @SerialName("visitId") val visitId: Long,
    @SerialName("placeName") val placeName: String,
    @SerialName("visitImage") val visitImage: String,
    @SerialName("visitedAt") val visitedAt: String,
)
