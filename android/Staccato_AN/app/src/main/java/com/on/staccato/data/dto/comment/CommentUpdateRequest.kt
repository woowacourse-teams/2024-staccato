package com.on.staccato.data.dto.comment

import kotlinx.serialization.SerialName

data class CommentUpdateRequest(
    @SerialName("content") val content: String,
)
