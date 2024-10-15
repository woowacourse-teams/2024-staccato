package com.on.staccato.data.dto.staccato

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaccatoCreationRequest(
    @SerialName("staccatoTitle") val staccatoTitle: String,
    @SerialName("memoryId") val memoryId: Long,
    @SerialName("placeName") val placeName: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("address") val address: String,
    @SerialName("visitedAt") val visitedAt: String,
    @SerialName("momentImageUrls") val staccatoImageUrls: List<String>,
)
