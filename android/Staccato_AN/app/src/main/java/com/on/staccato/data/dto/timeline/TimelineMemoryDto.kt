package com.on.staccato.data.dto.timeline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineMemoryDto(
    @SerialName("categoryId") val memoryId: Long,
    @SerialName("categoryTitle") val memoryTitle: String,
    @SerialName("categoryThumbnailUrl") val memoryThumbnailUrl: String? = null,
    @SerialName("startAt") val startAt: String? = null,
    @SerialName("endAt") val endAt: String? = null,
    @SerialName("description") val description: String? = null,
)
