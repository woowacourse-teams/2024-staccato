package com.on.staccato.data.dto.category

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryStaccatoDto(
    @SerialName("staccatoId") val staccatoId: Long,
    @SerialName("staccatoTitle") val staccatoTitle: String,
    @SerialName("staccatoImageUrl") val staccatoImageUrl: String? = null,
    @SerialName("visitedAt") val visitedAt: String,
)
