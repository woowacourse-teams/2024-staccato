package com.woowacourse.staccato.data.dto.visit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitResponse(
    @SerialName("visitId") val visitId: Long,
    @SerialName("placeName") val placeName: String,
    @SerialName("visitImages") val visitImages: List<String>,
    @SerialName("address") val address: String,
    @SerialName("visitedAt") val visitedAt: String,
    @SerialName("visitedCount") val visitedCount: Long,
    @SerialName("visitLogs") val visitLogs: List<VisitLogDto>,
)
