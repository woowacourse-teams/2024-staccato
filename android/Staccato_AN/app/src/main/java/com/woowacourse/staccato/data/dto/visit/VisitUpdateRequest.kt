package com.woowacourse.staccato.data.dto.visit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitUpdateRequest(
    @SerialName("visitImageUrls") val visitImageUrls: List<String>,
    @SerialName("visitedAt") val visitedAt: String,
)
