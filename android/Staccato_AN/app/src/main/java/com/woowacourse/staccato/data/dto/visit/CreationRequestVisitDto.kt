package com.woowacourse.staccato.data.dto.visit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreationRequestVisitDto(
    @SerialName("pin_id") val pinId: Long,
    @SerialName("visited_images") val visitedImages: List<String>,
    @SerialName("visited_at") val visitedAt: String,
    @SerialName("travel_id") val travelId: Long,
)
