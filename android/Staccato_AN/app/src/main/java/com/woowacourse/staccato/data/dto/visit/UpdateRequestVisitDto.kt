package com.woowacourse.staccato.data.dto.visit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequestVisitDto(
    @SerialName("visited_images") val visitedImages: List<String>,
    @SerialName("visited_at") val visitedAt: String,
)
