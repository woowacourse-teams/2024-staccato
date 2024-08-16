package com.woowacourse.staccato.data.dto.moment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitLogDto(
    @SerialName("commentId") val visitLogId: Long,
    @SerialName("memberId") val memberId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("memberImageUrl") val memberImageUrl: String,
    @SerialName("content") val content: String,
)
