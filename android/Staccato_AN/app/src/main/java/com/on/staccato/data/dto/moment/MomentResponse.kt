package com.on.staccato.data.dto.moment

import com.on.staccato.data.dto.comment.CommentDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MomentResponse(
    @SerialName("momentId") val momentId: Long,
    @SerialName("memoryId") val memoryId: Long,
    @SerialName("memoryTitle") val memoryTitle: String,
    @SerialName("staccatoTitle") val staccatoTitle: String,
    @SerialName("placeName") val placeName: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("momentImageUrls") val momentImageUrls: List<String>,
    @SerialName("address") val address: String,
    @SerialName("visitedAt") val visitedAt: String,
    @SerialName("feeling") val feeling: String,
    @SerialName("comments") val visitLogs: List<CommentDto>,
)