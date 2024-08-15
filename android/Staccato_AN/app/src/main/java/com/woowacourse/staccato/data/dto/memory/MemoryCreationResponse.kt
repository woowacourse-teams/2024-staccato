package com.woowacourse.staccato.data.dto.memory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemoryCreationResponse(
    @SerialName("travelId") val travelId: Long,
)
