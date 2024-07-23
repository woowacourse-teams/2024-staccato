package com.woowacourse.staccato.data.dto.visit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitCreationRequest(
    @SerialName("pinId") val pinId: Long,
    @SerialName("visitedImages") val visitedImages: List<String>,
    @SerialName("visitedAt") val visitedAt: String,
    @SerialName("travelId") val travelId: Long,
)
