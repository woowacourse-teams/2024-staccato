package com.woowacourse.staccato.data.dto.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    @SerialName("commentId") val commentId: Long,
    @SerialName("memberId") val memberId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("memberImageUrl") val memberImageUrl: String? = null,
    @SerialName("content") val content: String,
)
