package com.woowacourse.staccato.data.dto.visit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitUpdateRequest(
    @SerialName("visitedImages") val visitedImages: List<String>,
    @SerialName("visitedAt") val visitedAt: String,
)
