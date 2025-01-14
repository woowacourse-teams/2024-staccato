package com.on.staccato.data.dto.memory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemoryRequest(
    @SerialName("categoryThumbnailUrl") val memoryThumbnailUrl: String? = null,
    @SerialName("categoryTitle") val memoryTitle: String,
    @SerialName("description") val description: String? = null,
    @SerialName("startAt") val startAt: String? = null,
    @SerialName("endAt") val endAt: String? = null,
)
