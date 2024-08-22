package com.woowacourse.staccato.data.dto.moment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MomentCreationRequest(
    @SerialName("memoryId") val memoryId: Long,
    @SerialName("placeName") val placeName: String,
    @SerialName("latitude") val latitude: String,
    @SerialName("longitude") val longitude: String,
    @SerialName("address") val address: String,
    @SerialName("visitedAt") val visitedAt: String,
    @SerialName("momentImageUrls") val momentImageUrls: List<String>,
)
