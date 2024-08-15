package com.woowacourse.staccato.data.dto.memory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemoryUpdateRequest(
    @SerialName("travelThumbnailUrl") val travelThumbnailUrl: String? = null,
    @SerialName("travelTitle") val travelTitle: String,
    @SerialName("description") val description: String? = null,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
)
