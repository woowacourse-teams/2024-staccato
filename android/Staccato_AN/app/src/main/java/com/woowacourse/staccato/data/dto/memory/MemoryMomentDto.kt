package com.woowacourse.staccato.data.dto.memory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemoryMomentDto(
    @SerialName("momentId") val momentId: Long,
    @SerialName("placeName") val placeName: String,
    @SerialName("momentImageUrl") val momentImageUrl: String? = null,
    @SerialName("visitedAt") val visitedAt: String,
)
