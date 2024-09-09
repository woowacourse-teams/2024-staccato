package com.on.staccato.data.dto.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentRequest(
    @SerialName("momentId") val momentId: Long,
    @SerialName("content") val content: String,
)
