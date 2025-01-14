package com.on.staccato.data.dto.category

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryStaccatoDto(
    @SerialName("momentId") val staccatoId: Long,
    @SerialName("staccatoTitle") val staccatoTitle: String,
    @SerialName("momentImageUrl") val staccatoImageUrl: String? = null,
    @SerialName("visitedAt") val visitedAt: String,
)
