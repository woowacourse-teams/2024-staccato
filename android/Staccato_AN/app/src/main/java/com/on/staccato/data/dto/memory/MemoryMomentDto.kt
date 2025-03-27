package com.on.staccato.data.dto.memory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemoryMomentDto(
    @SerialName("momentId") val momentId: Long,
    @SerialName("staccatoTitle") val staccatoTitle: String,
    @SerialName("momentImageUrl") val staccatoImageUrl: String? = null,
    @SerialName("visitedAt") val visitedAt: String,
)
