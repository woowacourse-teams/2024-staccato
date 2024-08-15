package com.woowacourse.staccato.data.dto.moment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitResponse(
    @SerialName("visitId") val visitId: Long,
    @SerialName("placeName") val placeName: String,
    @SerialName("visitImageUrls") val visitImageUrls: List<String>,
    @SerialName("address") val address: String,
    @SerialName("visitedAt") val visitedAt: String,
    @SerialName("visitLogs") val visitLogs: List<VisitLogDto>,
)
