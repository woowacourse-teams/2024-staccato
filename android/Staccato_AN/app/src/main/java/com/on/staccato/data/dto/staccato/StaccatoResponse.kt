package com.on.staccato.data.dto.staccato

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaccatoResponse(
    @SerialName("momentId") val momentId: Long,
    @SerialName("memoryId") val memoryId: Long,
    @SerialName("memoryTitle") val memoryTitle: String,
    @SerialName("startAt") val startAt: String? = null,
    @SerialName("endAt") val endAt: String? = null,
    @SerialName("staccatoTitle") val staccatoTitle: String,
    @SerialName("placeName") val placeName: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("momentImageUrls") val momentImageUrls: List<String>,
    @SerialName("address") val address: String,
    @SerialName("visitedAt") val visitedAt: String,
    @SerialName("feeling") val feeling: String,
)
