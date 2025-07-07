package com.on.staccato.data.dto.staccato

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaccatoMarkerDto(
    @SerialName("staccatoId") val staccatoId: Long,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("staccatoColor") val color: String,
    @SerialName("title") val title: String,
    @SerialName("visitedAt") val visitedAt: String,
)
