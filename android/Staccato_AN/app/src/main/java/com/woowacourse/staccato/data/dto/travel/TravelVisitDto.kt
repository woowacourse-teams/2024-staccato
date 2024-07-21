package com.woowacourse.staccato.data.dto.travel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TravelVisitDto(
    @SerialName("visit_id") val visitId: Long,
    @SerialName("place_name") val placeName: String,
    @SerialName("visit_image") val visitImage: String,
    @SerialName("visited_at") val visitedAt: String,
)
