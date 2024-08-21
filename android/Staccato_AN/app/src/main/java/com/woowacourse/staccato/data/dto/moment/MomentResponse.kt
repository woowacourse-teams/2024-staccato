package com.woowacourse.staccato.data.dto.moment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MomentResponse(
    @SerialName("momentId") val momentId: Long,
    @SerialName("placeName") val placeName: String,
    @SerialName("momentImageUrls") val momentImageUrls: List<String>,
    @SerialName("address") val address: String,
    @SerialName("visitedAt") val visitedAt: String,
    @SerialName("feeling") val feeling: String,
    @SerialName("comments") val visitLogs: List<VisitLogDto>,
)
