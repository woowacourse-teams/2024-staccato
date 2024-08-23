package com.woowacourse.staccato.data.dto.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentsResponse(
    @SerialName("comments") val comments: List<CommentDto>
)
