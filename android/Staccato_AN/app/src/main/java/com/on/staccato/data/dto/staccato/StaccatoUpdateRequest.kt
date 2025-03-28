package com.on.staccato.data.dto.staccato

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaccatoUpdateRequest(
    @SerialName("staccatoTitle") val staccatoTitle: String,
    @SerialName("placeName") val placeName: String,
    @SerialName("address") val address: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("visitedAt") val visitedAt: String,
    @SerialName("categoryId") val categoryId: Long,
    @SerialName("staccatoImageUrls") val staccatoImageUrls: List<String>,
)
