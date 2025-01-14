package com.on.staccato.data.dto.memory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemoriesResponse(
    @SerialName("categories") val memories: List<MemoryCandidateResponse>,
)

@Serializable
data class MemoryCandidateResponse(
    @SerialName("categoryId") val memoryId: Long,
    @SerialName("categoryTitle") val memoryTitle: String,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
)
