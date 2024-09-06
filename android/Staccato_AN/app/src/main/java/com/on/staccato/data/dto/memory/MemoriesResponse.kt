package com.on.staccato.data.dto.memory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemoriesResponse(
    @SerialName("memories") val memories: List<MemoryCandidateResponse>,
)

@Serializable
data class MemoryCandidateResponse(
    @SerialName("memoryId") val memoryId: Long,
    @SerialName("memoryTitle") val memoryTitle: String,
)
