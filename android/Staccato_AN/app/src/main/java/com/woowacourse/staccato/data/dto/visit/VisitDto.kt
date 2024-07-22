package com.woowacourse.staccato.data.dto.visit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitDto(
    @SerialName("visit_id") val visitId: Long,
    @SerialName("place_name") val placeName: String,
    @SerialName("visited_images") val visitedImages: List<String>,
    @SerialName("address") val address: String,
    @SerialName("visited_at") val visitedAt: String,
    @SerialName("visited_count") val visitedCount: Long,
    @SerialName("visit_logs") val visitLogs: List<VisitLogDto>,
)
