package com.woowacourse.staccato.data.dto.moment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitUpdateRequest(
    @SerialName("placeName") val placeName: String,
    @SerialName("visitImageUrls") val visitImageUrls: List<String>,
)
