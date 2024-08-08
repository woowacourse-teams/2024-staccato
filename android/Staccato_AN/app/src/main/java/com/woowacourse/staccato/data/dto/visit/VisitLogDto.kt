package com.woowacourse.staccato.data.dto.visit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitLogDto(
    @SerialName("visitLogId") val visitLogId: Long,
    @SerialName("memberId") val memberId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("memberImageUrl") val memberImageUrl: String,
    @SerialName("content") val content: String,
)
