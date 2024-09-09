package com.on.staccato.data.dto.timeline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineMemoryDto(
    @SerialName("memoryId") val memoryId: Long,
    @SerialName("memoryTitle") val memoryTitle: String,
    @SerialName("memoryThumbnailUrl") val memoryThumbnailUrl: String? = null,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
    @SerialName("description") val description: String? = null,
)
