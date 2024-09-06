package com.on.staccato.data.dto.moment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MomentUpdateRequest(
    @SerialName("placeName") val placeName: String,
    @SerialName("momentImageUrls") val momentImageUrls: List<String>,
)
