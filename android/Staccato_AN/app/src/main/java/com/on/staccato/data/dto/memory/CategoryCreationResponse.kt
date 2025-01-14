package com.on.staccato.data.dto.memory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryCreationResponse(
    @SerialName("categoryId") val categoryId: Long,
)
